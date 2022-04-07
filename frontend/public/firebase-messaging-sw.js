

importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.10.0/firebase-messaging.js');

const FIREBASE_APP_NAME = "art-ping-firebase-app";
const API_KEY = "AIzaSyB4A7JycTYI9vfMy17118QPrKJ2b_sKLLc";
const PROJECT_ID = "artpingproject";
const SENDER_ID = "903622290694";
const APP_ID = "1:384665351333:web:3ba595e86f48d95e40494c";
const MEASUREMENT_ID = "G-2RTZN13F4P";
const VAPIDKEY =
  "BE6bDMXUJfUXOFbrZDv8VQQboi76qwyE69aAsXV6UCVylzajcyhPgjlotJn8WwnqNA9BksGPkfEQzAdhzflytyA";
const WEBAPP = 'http://localhost:3000';

const FIREBASE_CONFIG = {
  senderId: SENDER_ID,
  vapidKey: VAPIDKEY,
  apiKey: API_KEY,
  authDomain: PROJECT_ID + ".firebaseapp.com",
  databaseURL: "https://" + PROJECT_ID + "-default-rtdb.firebaseio.com",
  projectId: PROJECT_ID,
  storageBucket: PROJECT_ID + ".appspot.com",
  messagingSenderId: SENDER_ID,
  appId: APP_ID,
  measurementId: MEASUREMENT_ID,
};

firebase.initializeApp(FIREBASE_CONFIG);
const messaging = firebase.messaging();

console.log("firebase-messaging-sw", { messaging });

self.addEventListener('notificationclick', function(event) {
  if(!(event?.notification?.actions?.length) || event?.notification?.actions[0].action !== 'goto:check')
    return;
  let url = event?.notification?.data || WEBAPP;
  event.notification.close(); // Android needs explicit close.
  event.waitUntil(
      clients.matchAll({type: 'window'}).then( windowClients => {
          // Check if there is already a window/tab open with the target URL
          for (var i = 0; i < windowClients.length; i++) {
              var client = windowClients[i];
              // If so, just focus it.
              if (client.url === url && 'focus' in client) {
                  return client.focus();
              }
          }
          // If not, then open the target URL in a new window/tab.
          if (clients.openWindow) {
              return clients.openWindow(url);
          }
      })
  );
});

messaging.setBackgroundMessageHandler((payload) => {
    try{
      console.log("Handling background message", { payload });

      let title = payload?.data?.title || "ART-Ping";
      let body = payload?.data?.body || "Тук-тук! Есть кто живой?";
      let checkUrl = '/?ping=1'
      let confirmDeliveryUrl =
        payload?.data?.confirmDeliveryUrl || "/artping/iamhere/notification/";
      let icon =
        payload?.data?.icon || "/artping/assets/icons/notifications.png";

      let actions = [
        {
          action: "goto:check",
          title: "Перейти к Art-ping",
        },
      ];

      try {
        actions = JSON.parse(payload?.data?.actions);
      } catch (error) {
        console.error("Error parsing actions", error);
        console.warn("Will be used default actions", actions);
      }

      const options = {
        data: checkUrl,
        body,
        icon,
        actions,
        timestamp: payload?.data?.timestamp ? +payload?.data?.timestamp : -1,
        requireInteraction: !!payload?.data?.requireInteraction,
      };
      return messaging.getToken().then((token) => {

        fetch(confirmDeliveryUrl + token);
        return self.registration.showNotification(title, options);
      });
  }
  catch(e)
  {
    console.log(e);
  }
}
);

console.log("firebase-messaging-sw ******************************", {
  messaging,
});
