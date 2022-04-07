package com.artezio.artping.service;

import com.artezio.artping.controller.PageData;
import com.artezio.artping.controller.request.EmployeesTestsPageFilter;
import com.artezio.artping.data.exceptions.ArtPingException;
import com.artezio.artping.data.exceptions.ExceededChecksPerDayException;
import com.artezio.artping.data.exceptions.ParameterExceededException;
import com.artezio.artping.data.exceptions.WorkingTimeOverException;
import com.artezio.artping.data.repository.EmployeeSubscriptionRepository;
import com.artezio.artping.data.repository.EmployeeTestRepository;
import com.artezio.artping.data.repository.EventRepository;
import com.artezio.artping.data.repository.PointRepository;
import com.artezio.artping.dto.*;
import com.artezio.artping.dto.employee.response.employeeDayInfo.DayInfo;
import com.artezio.artping.dto.employee.response.employeeDayInfo.DayTestDto;
import com.artezio.artping.dto.employee.response.employeeDayInfo.EmployeeWithDayInfo;
import com.artezio.artping.entity.*;
import com.artezio.artping.entity.calendar.DayType;
import com.artezio.artping.entity.calendar.Event;
import com.artezio.artping.entity.office.Office;
import com.artezio.artping.entity.project.AbstractProjectEmployee;
import com.artezio.artping.entity.project.Project;
import com.artezio.artping.entity.user.Employee;
import com.artezio.artping.entity.user.Role;
import com.artezio.artping.service.schedulers.SchedulerService;
import com.artezio.artping.service.utils.DateUtils;
import com.artezio.artping.service.utils.LocalTimeUtils;
import com.artezio.artping.service.utils.RepositoryUtils;
import com.artezio.artping.service.utils.UserAgentUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.artezio.artping.entity.setting.SystemSettingsEnum.*;
import static com.artezio.artping.service.utils.DateUtils.getWeekends;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * Сервис для работы с проверками сотрудников
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeTestService {

    public static final String FIREBASE_REQUESTED_SUBSCRIPTION_WAS_NOT_FOUND = "Requested entity was not found";
    private final ArtPingProperties artPingProperties;
    private final SystemSettingsService systemSettingsService;
    private final EmployeeTestRepository repository;
    private final EmployeeSubscriptionRepository employeeSubscriptionRepository;
    private final EmployeeService employeeService;
    private final SchedulerService schedulerService;
    private final FirebaseNotificationService firebaseNotificationService;
    private final EventRepository eventRepository;
    private final CalendarService calendarService;
    private final MapperFacade mapper;
    private final RoleService roleService;
    private final PointRepository pointRepository;

    /**
     * Получение проверок сотрудника по его ИД
     *
     * @param employeeId ИД сотрудника
     * @return список проверок сотрудника
     */
    public List<EmployeeTestDto> findAllByEmployeeId(UUID employeeId) {
        return mapper.mapAsList(repository.findAllByEmployeeId(employeeId), EmployeeTestDto.class);
    }

    /**
     * Получение постраничного списка сотрудников с их проверками за определенный период по фильтру
     *
     * @param filter период, пагинация, фильтрация и сортировка
     * @return постраничный список сотрудников с данными о проверках
     */
    @Transactional
    public PageData<EmployeeWithDayInfo> findAllEmployeesByCriteria(EmployeesTestsPageFilter filter) {
        PageData<Employee> employeesPage = employeeService.getEmployeeEntitiesPage(filter);

        Employee currentEmployee = employeeService.getCurrentEmployee();
        ZoneId employeeTimezone;
        if (currentEmployee.getLogin().equals("admin")) {
            employeeTimezone = ZoneId.systemDefault();
        } else {
            employeeTimezone = ZoneId.of(currentEmployee.getBaseOffice().getTimezone());
        }

        List<EmployeeWithDayInfo> employeeWithDayInfoList = new ArrayList<>();
        for (Employee employee : employeesPage.getList()) {
            UUID employeeId = employee.getId();
            EmployeeWithDayInfo employeeWithDayInfo = new EmployeeWithDayInfo();
            employeeWithDayInfo.setId(employeeId);
            employeeWithDayInfo.setFirstName(employee.getFirstName());
            employeeWithDayInfo.setLastName(employee.getLastName());
            employeeWithDayInfo.setActive(employee.getUser().isActive());
            employeeWithDayInfo.setRoles(employee.getRoles().stream()
                    .map(r -> mapper.map(r, RoleInfo.class))
                    .collect(Collectors.toList())
            );

            List<DayInfo> dayInfos = new ArrayList<>();
            LocalDate currentDate = filter.getStartPeriod();
            LocalDate endDate = filter.getEndPeriod();

            Map<LocalDate, List<EmployeeTestDto>> employeeTestsForPeriod =
                    getEmployeeTestForPeriod(employeeId, currentDate, endDate);
            Map<LocalDate, Event> dayEventsForPeriod = calendarService.getDayEventsForPeriod(
                    employee.getCalendar().getId(), currentDate, endDate);

            Collection<DayOfWeek> weekends = getWeekends(employee);
            while (!currentDate.isAfter(endDate)) {
                DayInfo dayInfo = new DayInfo();
                dayInfo.setDate(currentDate);

                processTests(currentDate, employeeTestsForPeriod, dayInfo, employeeTimezone);

                Event dayEvent = dayEventsForPeriod.get(currentDate);
                DayType type;
                if (dayEvent == null) {
                    type = weekends.contains(currentDate.getDayOfWeek()) ? DayType.WEEKEND : DayType.WORKING_DAY;
                } else {
                    type = dayEvent.getType();
                }

                dayInfo.setType(type);

                dayInfos.add(dayInfo);
                currentDate = currentDate.plusDays(1);
            }
            employeeWithDayInfo.setDates(dayInfos);
            employeeWithDayInfoList.add(employeeWithDayInfo);
        }

        return new PageData<>(employeeWithDayInfoList, employeesPage.totalCount);
    }

    private static void processTests(LocalDate currentDate, Map<LocalDate,
            List<EmployeeTestDto>> employeeTestsForPeriod, DayInfo dayInfo, ZoneId employeeTimezone) {
        List<EmployeeTestDto> employeeTestsForDate = employeeTestsForPeriod.get(currentDate);
        if (employeeTestsForDate == null) {
            employeeTestsForDate = Collections.emptyList();
        }

        List<DayTestDto> tests = new ArrayList<>();
        employeeTestsForDate.stream()
                .filter(employeeTest -> employeeTest.getStatus() != TestStatusEnum.CANCELED)
                .forEach(employeeTest -> {
                    DayTestDto test = new DayTestDto();
                    test.setStartTime(employeeTest.getStartTime().withZoneSameInstant(employeeTimezone));
                    ZonedDateTime responseTime = employeeTest.getResponseTime();
                    if (responseTime != null) {
                        test.setResponseTime(responseTime.withZoneSameInstant(employeeTimezone));
                    }
                    test.setStatus(employeeTest.getStatus());
                    test.setPoint(employeeTest.getPoint());
                    tests.add(test);
                });
        tests.sort(Comparator.comparing(DayTestDto::getStartTime));
        dayInfo.setTests(tests);
    }

    private void runEmployeeTest(List<UUID> employeeIdsForTest,
                                 List<EmployeeTest> newEmployeeTestList) {
        // Все валидные подписки, на которые нужно разослать уведомления
        List<EmployeeSubscription> employeeSubscriptions =
                employeeSubscriptionRepository.findAllByValidAndEmployeeIdIn(true, employeeIdsForTest);

        if (!employeeSubscriptions.isEmpty()) {
            ///если у юзера нет подписок, то статус уведомления подписки меняем на НЕТ ПОДПИСОК
            List<UUID> employeeIdsWithSubscribes = employeeSubscriptions.stream()
                    .map(subscription -> subscription.getEmployee().getId())
                    .collect(Collectors.toList());

            for (EmployeeTest employeeTest : newEmployeeTestList) {
                if (!employeeIdsWithSubscribes.contains(employeeTest.getEmployee().getId())) {
                    employeeTest.setNotification(NotificationStatusEnum.NO_SUBSCRIPTION);
                }
            }

            List<String> employeeSubscriptionIds = new LinkedList<>();
            List<EmployeeSubscription> reducedSubscriptionList = new LinkedList<>();
            for (EmployeeSubscription s : employeeSubscriptions) {
                if (!employeeSubscriptionIds.contains(s.getSubscribeId())) {
                    employeeSubscriptionIds.add(s.getSubscribeId());
                    reducedSubscriptionList.add(s);
                }
            }

            NotificationsResponseDto notificationsResponse = firebaseNotificationService
                    .sendNotifications(new NotificationDTO(employeeSubscriptionIds));

            if (notificationsResponse.isSuccess()) {
                for (int i = 0; i < notificationsResponse.getNotificationResponseDtoList().size(); i++) {
                    //если не отправлено то подписка не валидна
                    EmployeeSubscription subscription = reducedSubscriptionList.get(i);
                    NotificationResponseDto notificationResponse = notificationsResponse.getNotificationResponseDtoList().get(i);

                    //если хотя бы одно уведомление отправлено, то устанавливаем статуст для уведомления проверки ОТПРАВЛЕНО
                    if (notificationResponse.isSuccess()) {
                        newEmployeeTestList.stream()
                                .filter((it) -> it.getEmployee().getId().compareTo(subscription.getEmployee().getId()) == 0
                                        && it.getNotification() != NotificationStatusEnum.SENT)
                                .forEach(employeeTest -> {
                                    employeeTest.setNotification(NotificationStatusEnum.SENT);
                                    employeeTest.setStatus(TestStatusEnum.IN_PROGRESS);
                                    employeeTest.setStartTime(ZonedDateTime.now());
                                });
                    } else if (FIREBASE_REQUESTED_SUBSCRIPTION_WAS_NOT_FOUND.equals(notificationResponse.getException().getMessage())) {
                        //если не отправлено то подписка не валидна
                        subscription.setValid(false);
                    }
                }
            }
        } else {
            newEmployeeTestList.forEach(
                    employeeTest -> employeeTest.setNotification(NotificationStatusEnum.NO_SUBSCRIPTION));
        }
        // сохраняем проверки для сотрудников
        repository.saveAll(newEmployeeTestList);
        employeeSubscriptionRepository.saveAll(employeeSubscriptions);

        // устанавливаем джоб, который по истечении времени проверки, установит статус НЕ ПРОЙДЕНА
        schedulerService.scheduleProcessingTestsWithNoResponseJob(
                employeeIdsForTest,
                DateUtils.addMinutes(new Date(), systemSettingsService.getSystemSetting(TEST_NO_RESPONSE_MINUTES).getIntValue())
        );
    }

    /**
     * Назначение проверки сотрудникам на текущий момент и её выполнение
     *
     * @param employees список сотрудников
     * @return сообщение о возникших ошибках во время создания, если их не возникло - null
     */
    @Transactional
    public EmployeeTestsResponseDto setCheckNowTestToEmployees(List<UUID> employees) {
        checkDoesCurrentEmployeeCanWorkWithTheseEmployees(employees);
        EmployeeTestsResponseDto response = new EmployeeTestsResponseDto();

        List<EmployeeTest> newEmployeeTestList = new ArrayList<>();
        try {
            for (UUID employeeId : employees) {
                newEmployeeTestList.add(
                        createEmployeeTest(employeeId, TestStatusEnum.PLANNED, ZonedDateTime.now()));
            }
            RepositoryUtils.saveAllIfNotEmpty(repository, newEmployeeTestList);
            runEmployeeTest(employees, newEmployeeTestList);
        } catch (ArtPingException ex) {
            response.setMessage(ex.getMessage());
        }

        return response;
    }

    /**
     * Получение последней проверки сотрудника
     *
     * @return информация о последней проверке сотрудника
     */
    @Transactional
    public EmployeeLastTestInfo getLastCurrentEmployeeCheck() {
        Employee employee = employeeService.getCurrentEmployee();
        List<EmployeeTestDto> tests = findAllByEmployeeId(employee.getId());
        ZonedDateTime now = ZonedDateTime.now();
        EmployeeTestDto testDto = tests.stream()
                .filter(test -> test.getStartTime().isBefore(now))
                .max((firstTest, lastTest) ->
                        firstTest.getStartTime().isAfter(lastTest.getStartTime()) ? 1 : -1)
                .orElse(new EmployeeTestDto());
        return mapper.map(testDto, EmployeeLastTestInfo.class);
    }

    /**
     * Планировка проверок переданным сотрудникам на определенный период
     *
     * @param requests идентификаторы сотрудников, кол-во назначаемых проверок и период
     * @return сообщение о возникших ошибках во время планировки, если их не возникло - null
     */
    @Transactional
    public EmployeeTestsResponseDto planForEmployees(EmployeePlannedTestsRequest requests) {
        checkDoesCurrentEmployeeCanWorkWithTheseEmployees(requests.getIds());
        LocalDate startDate = requests.getStartDate();
        LocalDate endDate = requests.getEndDate();
        Integer dailyChecks = requests.getDailyChecks();
        EmployeeTestsResponseDto response = new EmployeeTestsResponseDto();
        for (UUID id : requests.getIds()) {
            Employee employee = employeeService.findEmployeeById(id);
            response = planTestForEmployeeDays(employee, startDate, endDate, dailyChecks);
        }
        return response;
    }

    /**
     * Планировка проверок сотруднику на определённый период
     *
     * @param employee    сотрудник, которому назначаем проверки
     * @param startDate   дата начала периода
     * @param endDate     дата конца периода
     * @param dailyChecks количество необходимых проверок
     * @return сообщение о возникших ошибках во время планировки, если их не возникло - null
     */
    @Transactional
    public EmployeeTestsResponseDto planTestForEmployeeDays(Employee employee, LocalDate startDate,
                                                            LocalDate endDate, Integer dailyChecks) {
        EmployeeTestsResponseDto response = new EmployeeTestsResponseDto();
        LocalDate currentDate = startDate;
        LocalDate employeeDate = LocalDate.now(Clock.system(ZoneId.of(employee.getBaseOffice().getTimezone())));

        Collection<DayOfWeek> weekends = getWeekends(employee);
        while (!currentDate.isAfter(endDate)) {
            if (weekends.contains(currentDate.getDayOfWeek()) || currentDate.isBefore(employeeDate)) {
                currentDate = currentDate.plusDays(1);
                continue;
            }

            Set<Event> events = employee.getCalendar().getEvents();
            Event currentDayEvent = new Event();
            currentDayEvent.setDate(currentDate);
            if (!events.contains(currentDayEvent)) {
                response = planForEmployeeDay(employee, currentDate, dailyChecks);
            }
            currentDate = currentDate.plusDays(1);
        }
        return response;
    }

    /**
     * Планировка проверок сотруднику на определенный день
     *
     * @param employee    сотрудник, которому назначаем проверки
     * @param date        дата назначения проверок
     * @param dailyChecks количество необходимых проверок
     * @return сообщение о возникших ошибках во время планировки, если их не возникло - null
     */
    @Transactional
    public EmployeeTestsResponseDto planForEmployeeDay(Employee employee, LocalDate date, Integer dailyChecks) {
        EmployeeTestsResponseDto response = new EmployeeTestsResponseDto();

        try {
            // Рекомендованное(оно же максимальное) количество проверок в день
            int recommendedDailyChecks = systemSettingsService.getSystemSetting(RECOMMENDED_DAILY_CHECKS).getIntValue();

            if (dailyChecks > recommendedDailyChecks) {
                throw new ParameterExceededException("Количество проверок", recommendedDailyChecks);
            }
            ZoneId employeeTimeZone = ZoneId.of(employee.getBaseOffice().getTimezone());
            LocalTime currentTime = !date.isEqual(LocalDate.now(Clock.system(employeeTimeZone))) ?
                    LocalTime.MIN :
                    LocalTime.now(Clock.system(employeeTimeZone));

            // Временные рамки
            LocalTime beginningOfWorkingHours = employee.getCalendar().getWorkHoursFrom();
            LocalTime endOfWorkingHours = employee.getCalendar().getWorkHoursTo();
            LocalTime beginningOfTheLunchBreak = artPingProperties.getBeginningOfTheLunchBreak();
            LocalTime endOfTheLunchBreak = artPingProperties.getEndOfTheLunchBreak();

            // Проверка не закончился ли рабочий день
            checkIfWorkingTimeOver(currentTime, endOfWorkingHours);
            List<UUID> availableForCheck = new ArrayList<>();
            availableForCheck.add(employee.getId());
            /* Создание map для которой ключем является идентификатор сотрудника а значением - кол-во проверок за сегодня
               без учета проверок в статусе CANCELED
             */
            Long employeeTestsCount = getEmployeeTestsCountGroupByEmployeeId(employee.getId(), date);
            if (availableForCheck.isEmpty() || employeeTestsCount >= recommendedDailyChecks) {
                throw new ExceededChecksPerDayException();
            }

            //Поиск проверок для сотрудников из списка в статусе IN_PROGRESS
            Map<UUID, List<EmployeeTest>> employeeTestsInProgressMap = repository.findAllByStatusAndStartTimeAfterAndEmployeeId(
                    TestStatusEnum.IN_PROGRESS,
                    LocalDate.now().atStartOfDay().atZone(employeeTimeZone),
                    employee.getId()).stream()
                    .collect(Collectors.groupingBy(employeeTest -> employeeTest.getEmployee().getId()));

            Map<UUID, List<EmployeeTest>> employeeTestsPlannedMap = repository.findAllByStatusAndEmployeeIdIn(
                    TestStatusEnum.PLANNED, availableForCheck).stream()
                    .collect(Collectors.groupingBy(employeeTest -> employeeTest.getEmployee().getId()));

            List<EmployeeTest> newEmployeeTestList = new ArrayList<>();
            for (UUID employeeId : availableForCheck) {

                /* Определение момента с которого можно запускать проверку для сотрудника
                   это либо текущее время либо начало рабочего времени либо время запуска
                   проверки находящеся в статусе IN_PROGRESS + время отклика
                 */
                List<EmployeeTest> plannedTest = employeeTestsPlannedMap.get(employeeId);

                // Отбор только тех тестов, которые запланированы на текущий день
                if (plannedTest != null) {
                    plannedTest = plannedTest.stream()
                            .filter(test -> test.getStartTime().toLocalDate().isEqual(date))
                            .collect(Collectors.toList());
                }

                LocalTime beginningTimeForCheck =
                        getBeginningTimeForCheck(employeeTestsInProgressMap.get(employeeId), plannedTest, currentTime);

                // Список периодов
                List<PeriodDto> periods = new ArrayList<>();

                if (employeeTestsInProgressMap.containsKey(employeeId)) {
                    calculateLunchTimePeriodForInProgress(beginningOfWorkingHours, endOfWorkingHours, beginningOfTheLunchBreak, endOfTheLunchBreak, beginningTimeForCheck, periods);
                } else {
                    calculateLunchTimePeriod(beginningOfWorkingHours, endOfWorkingHours, beginningOfTheLunchBreak, endOfTheLunchBreak, beginningTimeForCheck, periods);
                }
                // Список доступных периодов для проверки, с учетом уже запланированных проверок
                int checksQuantity = availableForCheck.size();

                List<PeriodDto> availablePeriods = getAvailablePeriodsForCheckOnPeriods(periods, plannedTest, checksQuantity);

                if (!availablePeriods.isEmpty()) {
                    // кол-во возможных проверок c учетом уже созданных проверок для пользователя
                    long availableCheckCnt = Math.min(recommendedDailyChecks - employeeTestsCount, dailyChecks);
                    //при создании проверок учитываем кол-во уже созданных проверок для пользователя
                    for (long i = 0; i < availableCheckCnt; i++) {
                        // берем подходящий период из списка
                        Optional<PeriodDto> optional = availablePeriods.stream()
                                .filter(periodDto -> nonNull(periodDto) && periodDto.isValid())
                                .findAny();
                        if (optional.isPresent()) {
                            PeriodDto availablePeriod = optional.get();
                            // Получаем рандомное время в заданом периоде
                            LocalTime randomTime = LocalTimeUtils
                                    .randomInARange(availablePeriod.getStartTime(), availablePeriod.getEndTime());
                            // Создаем новую проверку
                            ZonedDateTime localTimeWithZone = LocalDateTime.of(date, randomTime)
                                    .atZone(employeeTimeZone);
                            newEmployeeTestList.add(createEmployeeTest(employeeId, TestStatusEnum.PLANNED, localTimeWithZone));

                            // удаляем текущий период
                            availablePeriods.remove(availablePeriod);
                            // добавляем в список 2 новых периода до проверки и после
                            availablePeriods.add(new PeriodDto(availablePeriod.getStartTime(),
                                    getAvailableTimeBeforeCheck(randomTime)));

                            availablePeriods.add(new PeriodDto(getAvailableTimeAfterCheck(randomTime),
                                    availablePeriod.getEndTime()));
                        }
                    }
                }
            }

            // сохраняем проверки в БД
            RepositoryUtils.saveAllIfNotEmpty(repository, newEmployeeTestList);

        } catch (ArtPingException ex) {
            response.setMessage(ex.getMessage());
            throw ex;
        }

        return response;
    }

    /**
     * Поиск тестов сотрудника за выбранный период с группировкой по дате и тестам на эту дату
     *
     * @param employeeId идентификатор сотрудника
     * @param startDate  дата начала периода
     * @param endDate    дата конца периода
     * @return тесты сотрудника за выбранный период
     */
    @Transactional
    public Map<LocalDate, List<EmployeeTestDto>> getEmployeeTestForPeriod(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        return employeeService.getEmployeeTestByEmployeeId(employeeId, startDate, endDate).stream()
                .collect(Collectors.groupingBy(test -> test.getStartTime().toLocalDate()));
    }

    /**
     * Получение статистики проверок выбранных сотрудников за определенный период
     *
     * @param employeeIds список сотрудников
     * @param startDate   дата начала периода
     * @param endDate     дата конца периода
     * @return имя, фамилия, статистика в процентах за этот период и числовые значения (всего тестов/успешные)
     * для каждого из дней этого периода
     */
    @Transactional
    public List<EmployeeTestStatisticDto> getEmployeesTestStatisticForPeriod(List<UUID> employeeIds, LocalDate startDate,
                                                                             LocalDate endDate) {
        List<EmployeeTestStatisticDto> employeeTestStatisticDtos = new ArrayList<>();
        for (UUID employeeId : employeeIds) {
            int allTestsCount = 0;
            int allSuccessTestsCount = 0;
            EmployeeTestStatisticDto employeeTestStatistic = new EmployeeTestStatisticDto();
            Employee employee = employeeService.findEmployeeById(employeeId);
            employeeTestStatistic.setFirstName(employee.getFirstName());
            employeeTestStatistic.setLastName(employee.getLastName());
            Map<LocalDate, List<EmployeeTestDto>> employeeTestForPeriod =
                    getEmployeeTestForPeriod(employeeId, startDate, endDate);
            Map<LocalDate, String> dayResult = new HashMap<>();
            LocalDate currentDate = startDate;
            Map<LocalDate, Event> dayEventsForPeriod =
                    calendarService.getDayEventsForPeriod(employee.getCalendar().getId(), startDate, endDate);
            Collection<DayOfWeek> weekends = getWeekends(employee);
            while (!currentDate.isAfter(endDate)) {
                Event event = dayEventsForPeriod.get(currentDate);
                DayType dayType;
                if (event == null) {
                    dayType = weekends.contains(currentDate.getDayOfWeek()) ? DayType.WEEKEND : DayType.WORKING_DAY;
                } else {
                    dayType = event.getType();
                }
                if (dayType == DayType.WORKING_DAY) {
                    List<EmployeeTestDto> tests = employeeTestForPeriod.get(currentDate);
                    if (tests != null) {
                        tests = tests.stream()
                                .filter(test -> test.getStatus() != TestStatusEnum.CANCELED)
                                .collect(Collectors.toList());
                        int successTests = (int) tests.stream()
                                .map(EmployeeTestDto::getStatus)
                                .filter(test -> test == TestStatusEnum.SUCCESS).count();
                        allTestsCount += tests.size();
                        allSuccessTestsCount += successTests;
                        dayResult.put(currentDate, tests.size() + "/" + successTests);
                    }
                } else {
                    dayResult.put(currentDate, getShortNonWorkingDayTypeName(dayType));
                }
                currentDate = currentDate.plusDays(1);
            }
            employeeTestStatistic.setDayResult(dayResult);
            double percentage = 0;
            if (allSuccessTestsCount != 0) {
                percentage = (double) Math.round(((double) allSuccessTestsCount / allTestsCount) * 10000) / 100;
            }
            employeeTestStatistic.setPercentage(percentage);
            employeeTestStatisticDtos.add(employeeTestStatistic);
        }

        return employeeTestStatisticDtos;
    }

    /**
     * Получение сотрудника с информацией о проверках
     *
     * @param request идентификатор сотрудника и период
     * @return информация о сотруднике и его проверках
     */
    @Transactional
    public List<EmployeeTestDto> getEmployeeTestForPeriod(EmployeeTestForPeriodRequest request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        return new ArrayList<>(employeeService.getEmployeeTestByEmployeeId(request.getId(), startDate, endDate));
    }

    private void calculateLunchTimePeriod(LocalTime beginningOfWorkingHours,
                                          LocalTime endOfWorkingHours, LocalTime beginningOfTheLunchBreak, LocalTime endOfTheLunchBreak,
                                          LocalTime beginningTimeForCheck, List<PeriodDto> periods) {
        PeriodDto periodBeforeLunch;
        PeriodDto periodAfterLunch;
        if (beginningTimeForCheck.isBefore(beginningOfTheLunchBreak)) {
            LocalTime startDurationBeforeLunch = LocalTimeUtils
                    .getMaxFrom(beginningOfWorkingHours, beginningTimeForCheck);
            periodBeforeLunch = new PeriodDto(startDurationBeforeLunch,
                    getAvailableTimeBeforeCheck(beginningOfTheLunchBreak));
            periodAfterLunch = new PeriodDto(endOfTheLunchBreak,
                    getAvailableTimeBeforeCheck(endOfWorkingHours));
            periods.add(periodBeforeLunch);
            periods.add(periodAfterLunch);
        } else {
            LocalTime startDurationAfterLunch = LocalTimeUtils
                    .getMaxFrom(endOfTheLunchBreak, beginningTimeForCheck);
            periodAfterLunch = new PeriodDto(startDurationAfterLunch,
                    getAvailableTimeBeforeCheck(endOfWorkingHours));
            periods.add(periodAfterLunch);
        }
    }

    private void calculateLunchTimePeriodForInProgress(LocalTime beginningOfWorkingHours,
                                                       LocalTime endOfWorkingHours, LocalTime beginningOfTheLunchBreak, LocalTime endOfTheLunchBreak,
                                                       LocalTime beginningTimeForCheck, List<PeriodDto> periods) {
        PeriodDto periodBeforeLunch;
        PeriodDto periodAfterLunch;
        LocalTime startDurationBeforeLunch = LocalTimeUtils
                .getMaxFrom(beginningTimeForCheck, beginningOfWorkingHours, beginningTimeForCheck);
        if (beginningTimeForCheck.isBefore(beginningOfTheLunchBreak)) {
            periodBeforeLunch = new PeriodDto(startDurationBeforeLunch,
                    getAvailableTimeBeforeCheck(beginningOfTheLunchBreak));
            periodAfterLunch = new PeriodDto(endOfTheLunchBreak,
                    getAvailableTimeBeforeCheck(endOfWorkingHours));
            periods.add(periodBeforeLunch);
            periods.add(periodAfterLunch);
        } else {
            LocalTime startDurationAfterLunch = LocalTimeUtils
                    .getMaxFrom(beginningTimeForCheck, endOfTheLunchBreak, beginningTimeForCheck);
            periodAfterLunch = new PeriodDto(startDurationAfterLunch,
                    getAvailableTimeBeforeCheck(endOfWorkingHours));
            periods.add(periodAfterLunch);
        }
    }

    /*
      Нахождение доступных периодов для проверок, с учетом уже запланированных проверок.
     */
    private List<PeriodDto> getAvailablePeriodsForCheckOnPeriods(List<PeriodDto> periods,
                                                                 List<EmployeeTest> plannedTests, int checksQuantity) {
        List<PeriodDto> avaliablePeriods = new ArrayList<>();

        if (isEmpty(periods)) {
            return avaliablePeriods;
        }

        if (isEmpty(plannedTests)) {
            return periods;
        }

        for (PeriodDto period : periods) {
            // Выбираем startTime запланированных проверки для определенного периода.
            // В нашем случае для периодов beforeLunchBreak и afterLunchBreak.
            // Также сортируем по дате создания.
            List<LocalTime> testsStartTimes = plannedTests.stream()
                    .map(EmployeeTest::getStartTime)
                    .map(ZonedDateTime::toLocalTime)
                    .filter(testStartTime -> !testStartTime.isAfter(period.getEndTime())
                            && !testStartTime.isBefore(period.getStartTime()))
                    .sorted(LocalTime::compareTo)
                    .collect(Collectors.toList());

            // Начинаем поиск доступных периодов с даты начала большого периода (beforeLunchBreak или afterLunchBreak).
            // Далее по порядку, от проверки к проверке находим окно(период), в которм
            // можем создать новую проверку. см пункт 9.2 и 9.3 алгоритма
            // "Создание автоматической проверки".
            LocalTime time = period.getStartTime();
            for (LocalTime testStartTime : testsStartTimes) {
                avaliablePeriods.add(new PeriodDto(time, getAvailableTimeBeforeCheck(testStartTime)));
                time = getAvailableTimeAfterCheck(testStartTime);
            }

            avaliablePeriods.add(new PeriodDto(time, period.getEndTime()));

        }

        return avaliablePeriods;
    }

    private void checkIfWorkingTimeOver(LocalTime currentTime, LocalTime endOfWorkingHours) {
        if (!currentTime.isBefore(endOfWorkingHours)) {
            throw new WorkingTimeOverException();
        }
    }

    /* Если для сотрудника найдена активная проверка, то метод возвращает
       max([EmployeeTest.StartTime] + "Время ожидания ответа на проверку (мин)", текущее время) иначе текущее время
       с учетом задержки scheduler (запускается каждые n минут) и с учетом проверок в статусе PLANNED
       при отсутствии подписки(такая проверка остается в статусе PLANNED).
     */
    private LocalTime getBeginningTimeForCheck(List<EmployeeTest> testsInProgress,
                                               List<EmployeeTest> testsPlanned, LocalTime currentTime) {
        if (isNotEmpty(testsInProgress)) {
            LocalTime lastTestStartTime = LocalTimeUtils.getMaxFrom(testsInProgress.stream()
                    .map(EmployeeTest::getStartTime)
                    .map(ZonedDateTime::toLocalTime)
                    .collect(Collectors.toList()));

            if (nonNull(lastTestStartTime)) {
                LocalTime beginningTimeAfterLastCheck = getAvailableTimeAfterCheck(lastTestStartTime);

                return LocalTimeUtils.getMaxFrom(currentTime, beginningTimeAfterLastCheck);
            }
        }

        // Если есть проверки в статусе PLANNED
        if (isNotEmpty(testsPlanned)) {
            // Находим startTime проверки в статусе PLANNED, у которой время запуска startTime
            // до currentTime и ближайшее к currentTime.(случаи когда нет подписки
            // или scheduler не успел сменить статус проверки)
            Optional<LocalTime> lastTestPlannedStartTime = testsPlanned.stream()
                    .map(EmployeeTest::getStartTime)
                    .map(ZonedDateTime::toLocalTime)
                    .filter(testStartTime -> testStartTime.isBefore(currentTime))
                    .max(LocalTime::compareTo);

            // Если такая есть,проверяем getAvailableTimeAfterCheck не позже ли чем currentTime
            if (lastTestPlannedStartTime.isPresent()) {
                LocalTime avaliableTimeAfterCheck = getAvailableTimeAfterCheck(
                        lastTestPlannedStartTime.get());
                return avaliableTimeAfterCheck.isAfter(currentTime) ? avaliableTimeAfterCheck : currentTime;
            }

        }

        return currentTime;
    }

    // возвращает время в которое можно создать новую проверку после времени старта уже созданного теста
    private LocalTime getAvailableTimeAfterCheck(LocalTime time) {
        if (isNull(time)) {
            throw new IllegalArgumentException();
        }
        return time.plusMinutes(systemSettingsService.getSystemSetting(TEST_NO_RESPONSE_MINUTES).getIntValue());
    }

    // возвращает время в которое можно создать новую проверку с учетом времени отклика
    private LocalTime getAvailableTimeBeforeCheck(LocalTime time) {
        if (isNull(time)) {
            throw new IllegalArgumentException();
        }
        return time.minusMinutes(systemSettingsService.getSystemSetting(TEST_NO_RESPONSE_MINUTES).getIntValue());
    }

    /**
     * Метод возвращает map для которой ключем является идентификатор сотрудника, а значением кол-во проверок за сегодня без учета отмененных проверок
     *
     * @param employeeId идентификатор сотрудника
     * @param day        день за который необходимо получить статистику
     * @return статистика по проверкам за день для списка пользователей
     */
    public Long getEmployeeTestsCountGroupByEmployeeId(UUID employeeId, LocalDate day) {
        ZonedDateTime startOfToday = day.atStartOfDay()
                .atZone(ZoneId.of(employeeService.findEmployeeById(employeeId).getBaseOffice().getTimezone()));
        ZonedDateTime startOfTomorrow = day.plusDays(1).atStartOfDay()
                .atZone(ZoneId.of(employeeService.findEmployeeById(employeeId).getBaseOffice().getTimezone()));
        return repository.countEmployeeTestInPeriod(employeeId, startOfToday, startOfTomorrow);
    }

    /**
     * Обновление статуса проверки на SUCCESS (пройдена успешно)
     *
     * @param userAgent информация о клиенте
     */
    @Transactional
    public void responseToEmployeeTests(PointDto pointDto, String userAgent) {
        List<EmployeeTest> currentEmployeeTestsInProgress = currentEmployeeTestsInProgress();
        if (currentEmployeeTestsInProgress.isEmpty()) {
            return;
        }

        ZonedDateTime now = ZonedDateTime.now();

        for (EmployeeTest employeeTest : currentEmployeeTestsInProgress) {
            employeeTest.setResponseTime(now);
            ZonedDateTime successExpirationTime = employeeTest.getStartTime()
                    .plusMinutes(systemSettingsService.getSystemSetting(TEST_SUCCESSFUL_MINUTES).getIntValue());

            if (successExpirationTime.isBefore(now)) {
                employeeTest.setStatus(TestStatusEnum.NOT_SUCCESS);
            } else {
                employeeTest.setStatus(TestStatusEnum.SUCCESS);
            }

            employeeTest.setUserAgent(userAgent);
            employeeTest.setDeviceType(UserAgentUtils.detectDeviceType(userAgent));
            employeeTest.setPoint(pointRepository.save(mapper.map(pointDto, Point.class)));
            repository.save(employeeTest);
        }
    }

    private List<EmployeeTest> currentEmployeeTestsInProgress() {
        Employee currentEmployee = employeeService.getCurrentEmployee();
        if (currentEmployee == null) {
            throw new ArtPingException("You can't pass checks as a super user!");
        }
        return repository.findAllByStatusAndEmployeeId(
                TestStatusEnum.IN_PROGRESS,
                currentEmployee.getId()
        );
    }

    /**
     * Обновление статуса проверки для сотрудников на NO_RESPONSE или CANCELED по шедулеру
     *
     * @param employeeIds идентификаторы сотрудников
     */
    @Transactional
    public void processInProgressTestsWithNoResponse(List<UUID> employeeIds) {
        List<EmployeeTest> employeeTests = repository
                .findAllByStatusAndEmployeeIdIn(TestStatusEnum.IN_PROGRESS, employeeIds);

        List<Event> vacationEventsList = buildEventDayList(employeeTests,
                LocalDate.now()); // получаем отпуска и больничные за сегодня
        treatEmployeeTestsWithNoResponse(employeeTests, vacationEventsList);
    }

    /**
     * Обновление по шедулеру статуса безответных PLANNED проверок
     */
    @Transactional
    public void processPlannedTestsWithNoResponse() {
        List<EmployeeTest> plannedExpiredTests = repository.findByStatusAndResponseTimeIsNullAndStartTimeBefore(
                TestStatusEnum.PLANNED,
                ZonedDateTime.now()
                        .minusMinutes(systemSettingsService.getSystemSetting(TEST_NO_RESPONSE_MINUTES).getIntValue())
        );

        List<Event> vacationEventsList = buildEventDayList(plannedExpiredTests, LocalDate.now()); // получаем отпуска и больничные за сегодня
        treatEmployeeTestsWithNoResponse(plannedExpiredTests, vacationEventsList);
    }

    /**
     * Получаем отпуска и больничные сотрудников из employeeTests за даты dates
     *
     * @param employeeTests тесты
     * @param dates         даты
     * @return List объектов Event с датой и id сотрудника
     */
    private List<Event> buildEventDayList(List<EmployeeTest> employeeTests,
                                          LocalDate... dates) {
        if (employeeTests.isEmpty()) {
            return Collections.emptyList();
        }
        List<UUID> employeeIds = employeeTests.stream()
                .map(employeeTest -> employeeTest.getEmployee().getId())
                .collect(Collectors.toList());
        Set<UUID> calendarIds = employeeIds.stream().map((id) -> {
            Employee employee = employeeService.findEmployeeById(id);
            return employee.getCalendar().getId();
        }).collect(Collectors.toSet());

        List<Event> vacationEventsList = new ArrayList<>();
        for (LocalDate date : dates) {
            vacationEventsList.addAll(eventRepository.findEventsByCalendarIdInAndDate(calendarIds, date));
        }
        return vacationEventsList;
    }

    private void treatEmployeeTestsWithNoResponse(List<EmployeeTest> employeeTests,
                                                  List<Event> vacationEventsList) {
        if (employeeTests.isEmpty()) {
            return;
        }
        employeeTests.forEach(t -> {
            Event employeeEventsDay = vacationEventsList.stream()
                    .filter(event -> event.getCalendar().getId().equals(t.getEmployee().getCalendar().getId()))
                    .findFirst()
                    .orElse(null);

            if (nonNull(employeeEventsDay)) {
                // у сотрудника больничный или отпуск сегодня, отменяем проверку
                t.setStatus(TestStatusEnum.CANCELED);
            } else {
                // сотрудник не ответил в отведенное время
                t.setStatus(TestStatusEnum.NO_RESPONSE);
            }
        });
        repository.saveAll(employeeTests);
    }

    /**
     * Обновление статуса у проверок на NO_RESPONSE по шедулеру ночью
     */
    @Transactional
    public void processEmployeeAllTestsWithNoResponse() {
        List<EmployeeTest> employeeExpiredTests = repository.findByStatusAndResponseTimeIsNullAndStartTimeBefore(
                TestStatusEnum.IN_PROGRESS,
                ZonedDateTime.now()
                        .minusMinutes(systemSettingsService.getSystemSetting(TEST_NO_RESPONSE_MINUTES).getIntValue())
        );

        // получаем отпуска и больничные за сегодня и за вчера, т.к. шедулер запускается ночью после полуночи
        List<Event> vacationEventsList = buildEventDayList(employeeExpiredTests, LocalDate.now(), LocalDate.now().minusDays(1));
        treatEmployeeTestsWithNoResponse(employeeExpiredTests, vacationEventsList);
    }

    /**
     * Обновление статуса уведомления проверки на RECEIVED (Получено)
     *
     * @param subscribeId идентификатор подписки firebase
     */
    @Transactional
    public void changeTestStatusNotificationToReceived(String subscribeId) {

        List<EmployeeSubscription> employeeSubscriptions = employeeSubscriptionRepository
                .findBySubscribeId(subscribeId);
        if (employeeSubscriptions.isEmpty()) {
            return;
        }
        List<UUID> employeeIds = employeeSubscriptions
                .stream()
                .map(EmployeeSubscription::getId)
                .collect(Collectors.toList());

        List<EmployeeTest> employeeTests = repository
                .findAllByStatusAndEmployeeIdIn(TestStatusEnum.IN_PROGRESS, employeeIds);
        if (employeeTests.isEmpty()) {
            return;
        }

        employeeTests
                .forEach(employeeTest -> employeeTest.setNotification(NotificationStatusEnum.RECEIVED));

        repository.saveAll(employeeTests);
    }

    /**
     * Запуск запланированных проверок, для которых подошло время старта
     */
    @Transactional
    public void runPlannedTest() {
        //получаем все запланированные проверки, которые нужно выполнить, за промежуток времени - 5 минут
        List<EmployeeTest> plannedEmployeeTests = repository
                .findByStatusAndStartTimeBefore(TestStatusEnum.PLANNED, ZonedDateTime.now());
        if (!plannedEmployeeTests.isEmpty()) {
            List<UUID> employeeIds = plannedEmployeeTests.stream()
                    .map(employeeTest -> employeeTest.getEmployee().getId())
                    .collect(Collectors.toList());
            runEmployeeTest(employeeIds, plannedEmployeeTests);
        }
    }


    /**
     * Метод для отмены PLANNED проверок для списка сотрудников за определённый период
     *
     * @param employeeIds список идентификаторов сотрудников
     * @param startDate   начальная дата
     * @param endDate     конечная дата
     * @return статус выполнения, если null - никаких ошибок не возникло
     */
    @Transactional
    public EmployeeTestsResponseDto cancelEmployeesAutoChecks(List<UUID> employeeIds, LocalDate startDate,
                                                              LocalDate endDate) {
        EmployeeTestsResponseDto response = new EmployeeTestsResponseDto();
        try {
            List<EmployeeTest> employeeTests = new ArrayList<>();
            for (UUID employeeId : employeeIds) {
                Employee employee = employeeService.findEmployeeById(employeeId);
                ZoneId zoneId = ZoneId.of(employee.getBaseOffice().getTimezone());
                ZonedDateTime startPeriod;
                if (startDate.isEqual(LocalDate.now())) {
                    startPeriod = ZonedDateTime.now();
                } else {
                    startPeriod = startDate.atStartOfDay(zoneId);
                }
                ZonedDateTime endPeriod = endDate.plusDays(1).atStartOfDay(zoneId);
                employeeTests.addAll(repository.findForPeriod(employeeId, startPeriod, endPeriod).stream()
                        .filter(test -> test.getStatus() == TestStatusEnum.PLANNED)
                        .peek(test -> test.setStatus(TestStatusEnum.CANCELED))
                        .collect(Collectors.toList()));
            }
            repository.saveAll(employeeTests);
        } catch (ArtPingException ex) {
            response.setMessage(ex.getMessage());
        }
        return response;
    }

    private EmployeeTest createEmployeeTest(UUID employeeId, TestStatusEnum status, ZonedDateTime startTime) {
        EmployeeTest employeeTest = new EmployeeTest();
        employeeTest.setEmployee(employeeService.findEmployeeById(employeeId));
        employeeTest.setStartTime(startTime);
        employeeTest.setStatus(status);
        employeeTest.setNotification(NotificationStatusEnum.NOT_SENT);
        return employeeTest;
    }

    private void checkDoesCurrentEmployeeCanWorkWithTheseEmployees(List<UUID> employees) {
        // Проверка доступа в зависимости от роли
        Employee currentEmployee = employeeService.getCurrentEmployee();
        Role adminRole = roleService.findByCode(RoleEnum.ADMIN.getCode());
        Set<Role> employeeRoles = currentEmployee.getRoles();
        if (!employeeRoles.contains(adminRole)) {
            Set<UUID> managedEmployees = new HashSet<>();
            if (employeeRoles.contains(roleService.findByCode(RoleEnum.PM.getCode()))) {
                managedEmployees.addAll(currentEmployee.getProjectPMs().stream()
                        .map(AbstractProjectEmployee::getProject)
                        .map(Project::getProjectEmployees)
                        .flatMap(Collection::stream)
                        .map(AbstractProjectEmployee::getEmployee)
                        .map(Employee::getId)
                        .collect(Collectors.toSet()));
            }
            if (employeeRoles.contains(roleService.findByCode(RoleEnum.DIRECTOR.getCode())) ||
                    employeeRoles.contains(roleService.findByCode(RoleEnum.HR.getCode()))) {
                Set<Office> offices = new HashSet<>(currentEmployee.getOffices());
                offices.addAll(currentEmployee.getManagedOffices());
                Set<Employee> officeEmployees = new HashSet<>();
                offices.forEach(office -> officeEmployees.addAll(employeeService.findAllByOfficeId(office.getId())));
                managedEmployees.addAll(officeEmployees.stream().map(Employee::getId).collect(Collectors.toSet()));
            }
            if (!managedEmployees.containsAll(employees)) {
                throw new SecurityException("У текущего сотрудника недостаточно прав для этой операции");
            }
        }
    }

    private String getShortNonWorkingDayTypeName(DayType dayType) {
        String name;
        switch (dayType) {
            case VACATION:
                name = "О";
                break;
            case SICK:
                name = "Б";
                break;
            case SICKNESS:
                name = "БЛ";
                break;
            case WEEKEND:
                name = "В";
                break;
            default:
                name = "НР";
                break;
        }
        return name;
    }
}
