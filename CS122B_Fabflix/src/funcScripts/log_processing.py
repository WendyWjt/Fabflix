# log_processing.py

import os
from pathlib import Path


contextPath = "C:\\Users\\98552\\Desktop\\122B_Test\\timeLoggerDemo"

class log_processing:
    def __init__(self):
        self.logDirPath = contextPath
        print("LogDirPath = " + self.logDirPath)


    def run(self):
        logFileList = self.getLogFileList()
        curFile = None
        if logFileList:
            for i in range(len(logFileList)):
                curFile = logFileList[i]
                print("-" * 10)
                print("Parse " + curFile.name)
                if curFile.is_file():
                    timeList = self.parseLogFile(curFile)
                    avg = sum(timeList) / len(timeList) / 100000
                    print(curFile.name + ": average time = " + str(avg))
                else:
                    tjTimeList = []
                    tsTimeList = []
                    subLogFileList = list(curFile.iterdir())
                    for f in subLogFileList:
                        if "tj" in f.name:
                            tjTimeList.extend(self.parseLogFile(f))
                        else:
                            tsTimeList.extend(self.parseLogFile(f))
                    avg_tj = sum(tjTimeList) / len(tjTimeList) / 100000
                    avg_ts = sum(tsTimeList) / len(tsTimeList) / 100000
                    print(curFile.name + ": avg_tj = " + str(avg_tj) + "; avg_ts = " + str(avg_ts))


    def getLogFileList(self):
        logDir = Path(self.logDirPath)
        if (logDir.exists()):
            return list(logDir.iterdir())
        else:
            print("GetLogFileList: Error: logDir not exists. logDir = " + self.logDirPath)
            return None


    def parseLogFile(self, p: Path):
        if (p.exists() and not p.is_dir()):
            result = []
            try:
                f = open(p, "r")
                for line in f:
                    line_lst = line.split()
                    result.append(int(line_lst[-1]))
                f.close()
                return result
            except:
                print("ParseLogFile: Error: File Not Found. FilePath = " + str(p))
                return None
        else:
            print("ParseLogFile: Error: Path points to a directory. FilePath = " + str(p))
            return None


if __name__ == "__main__":
    lp = log_processing()
    lp.run()