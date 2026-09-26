# Build Stage
FROM node:22-alpine AS build-stage
WORKDIR /app
COPY app/package.json app/yarn.lock ./
RUN yarn install --frozen-lockfile
COPY app/ ./
RUN yarn build

# Production Stage
FROM nginx:1.25.1-alpine AS production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
