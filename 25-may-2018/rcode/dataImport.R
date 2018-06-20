
require(strucchange)
require(tidyverse)


rm(list=ls())
source('C:/Users/maska/OneDrive/Documents/MASON/21-may-2018/rcode/funcs.R')

# Where and what data? ----------------------------------------------------
num.folder <- c(0.70) #, 0.75, 0.80, 0.85, 0.90, 0.95, 1.00)
num.filename <- c(0:9)

filenames <- str.num("reservoir-timeSeries-%d.txt", num.filename)
# folders <- str.num("Shift%1.2f_Sc1_Runs5", num.folder)
folders <- str.num("%1.2f_runs", num.folder)
files <- file.list("C:/Users/maska/OneDrive/Documents/MASON/21-may-2018/%s", filenames)

# Parse the data --------------------------------------------------------
# this section reads a bunch of files and puts the breakpoint data into a csv file.
# the data saved includes population and fitted storage values at each breakpoint and the 
# start and end of the timeseries.

file_path <- "C:/Users/maska/OneDrive/Documents/MASON/21-may-2018/0.7_shift.csv"

i <- '0.7 shift'
for (j in filenames) {
  # folder <- str.num("Shift%1.2f_Sc1_Runs5", i)
  # folder <- str.num("%1.2f_runs", i)
  file <- sprintf("C:/Users/maska/OneDrive/Documents/MASON/21-may-2018/%s", j)
  stress <- read_delim(file, ' ')
  new.df <- find.bp(stress, window=120, i, j)
  if(file.exists(file_path)){
    write_csv(new.df, file_path, append=TRUE, col_names=FALSE)
  } else {
    write_csv(new.df, file_path, append=TRUE, col_names=TRUE)
  }
}



# Parse zee data --------------------------------------------------------
# this section reads a bunch of files and puts the breakpoint data into a csv file.
# the data saved includes population and fitted storage values at each breakpoint and the 
# start and end of the timeseries.

file_path <- "C:/Users/maska/OneDrive/Documents/MASON/03-may-2018"

for (i in num.folder) {
  for (j in filenames) {
    # folder <- str.num("Shift%1.2f_Sc1_Runs5", i)
    folder <- str.num("%1.2f_runs", i)
    file <- sprintf("G:/My Drive/BerglundResearch/model_data/%s/%s", folder, j)
    stress <- read_delim(file, ' ')
    new.df <- find.bp(stress, window=120, i, j)
    if(file.exists(file_path)){
      write_csv(new.df, file_path, append=TRUE, col_names=FALSE)
    } else {
      write_csv(new.df, file_path, append=TRUE, col_names=TRUE)
    }
  }
}


# individual file import or something like that ---------------------------
shift <- 0.70
num <- 0

filename <- str.num("reservoir-timeSeries-%d.txt", num)
folder <- str.num("%1.2f_runs", shift)
file <- sprintf("C:/Users/maska/OneDrive/Documents/MASON/03-may-2018/%s", filename)
stress <- read_delim(file, ' ')

make_graph(stress$storage, stress$population, 120, 'testing shift gradient', filename, 'shift 0.70')

