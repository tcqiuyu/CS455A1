all: compile
	@echo -e '[INFO] Done!'
clean:
	@echo -e '[INFO] Cleaning up...'
	@rm -rf cs455/**/*.class cs455/**/**/*.class
compile:
	@echo -e '[INFO] Comiling the source..'
	@javac -d . cs455/**/*.java cs455/**/**/*.java