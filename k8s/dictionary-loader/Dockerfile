FROM postgres:16-alpine

COPY backend/game-service/dictionary /dictionary
COPY scripts/load-dictionary.sh /load-dictionary.sh
COPY scripts/filter-motus-dictionary.awk /filter-motus-dictionary.awk
RUN chmod +x /load-dictionary.sh

ENTRYPOINT ["/bin/sh", "/load-dictionary.sh"]
