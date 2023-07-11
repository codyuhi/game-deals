# Develop stage
FROM node:18-alpine AS develop-stage
WORKDIR /app
COPY ./app /app
RUN yarn install

# Build stage
FROM develop-stage AS build-stage
RUN yarn build

# Production stage
FROM nginx:1.25.1-alpine AS production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]