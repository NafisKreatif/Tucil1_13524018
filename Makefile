SRC_DIR     := src
MAIN        := ./$(SRC_DIR)/backend/go

# Define phony targets
.PHONY: run

# Run the program
run: 
	go run $(MAIN)