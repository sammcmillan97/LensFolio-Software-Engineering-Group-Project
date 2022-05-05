fuser -k 9500/tcp || true
export $(cat staging-identityprovider/.env | xargs -d '\n')
SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE java -jar staging-identityprovider/libs/identityprovider-0.0.1-SNAPSHOT.jar --spring.application.name=identity-provider --grpc.server.port=9500
