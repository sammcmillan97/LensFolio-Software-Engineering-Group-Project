fuser -k 10500/tcp || true
export $(cat production-identityprovider/.env | xargs -d '\n')
SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE java -jar production-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar \
    --spring.application.name=identity-provider \
    --grpc.server.port=10500