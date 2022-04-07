FROM node:12.18.3

RUN mkdir /app
WORKDIR /app
COPY ./src ./src
COPY ./public ./public
COPY ./package.json ./
COPY ./tsconfig.json ./

RUN npm install
RUN npm run build

FROM nginx
COPY --from=0 /app/build/ /usr/share/nginx/html/artping
EXPOSE 8006