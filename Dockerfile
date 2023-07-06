# Build stage
FROM node:18 AS builder
WORKDIR /app
COPY ./app /app
RUN yarn build

FROM nginx:1.25.1-alpine AS runtime
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]