# Build Stage
FROM node:26-alpine AS build-stage
WORKDIR /app
RUN command -v yarn >/dev/null 2>&1 || npm install -g yarn
COPY app/package.json app/yarn.lock ./
RUN yarn install --frozen-lockfile
COPY app/ ./
RUN yarn build

# Production Stage
FROM nginxinc/nginx-unprivileged:1.31-alpine AS production-stage
COPY --from=build-stage /app/dist /usr/share/nginx/html
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
