CC ?= gcc
CFLAGS ?= -Wall -Wextra -pedantic -std=c99 -O2
TARGET = password_generator

SRCS = main.c

all: $(TARGET)

$(TARGET): $(SRCS)
	$(CC) $(CFLAGS) $(SRCS) -o $(TARGET)

clean:
	rm -f $(TARGET) $(TARGET).exe *.o

.PHONY: all clean

