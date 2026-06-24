FROM nginx:1.27-alpine

COPY frontend /usr/share/nginx/html
COPY k8s/frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY k8s/frontend/config.k8s.js /usr/share/nginx/html/config.js
