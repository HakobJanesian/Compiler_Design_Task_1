$(eval TMP := $(shell mktemp)) 
C = "javac" 
COMPILER = "LexicalAnalyzer" 
INP0 = "input0.txt" 
OUT0 = "./output0.txt" 
TMP0 = "/tmp/output0.txt" 
DIFF = "diff" 
CMD = "/usr/bin/diff /tmp/output0.txt ./output0.txt" 
all: 
   $(C) $(COMPILER).java 
test: 
   java $(COMPILER) $(INP0) > $(TMP0) 
check: 
ifeq ("$(shell $(DIFF) $(TMP0) $(OUT0) ; echo $$?)", "0") 
   @echo "(:" 
else 
   @echo ":/" 
endif