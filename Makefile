.PHONY: compile
compile:
	./gradlew clean build

.PHONY: build
build: compile
	docker-compose -f docker-compose.yml build

.PHONY: run
run: build
	docker-compose -f docker-compose.yml up

.PHONY: stop
stop:
	docker-compose -f docker-compose.yml down
