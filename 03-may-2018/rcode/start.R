
require(strucchange)
require(tidyverse)


rm(list=ls())
source('funcs.R')


# Where and what data? ----------------------------------------------------
num.folder <- c(0.70, 0.75, 0.80, 0.85, 0.90, 0.95, 1.00)
num.filename <- c(0, 1, 2, 3, 4)

filenames <- str.num("reservoir-timeSeries-%d.txt", num.filename)
folders <- str.num("Shift%1.2f_Sc1_Runs5", num.folder)
files <- file.list("G:/My Drive/BerglundResearch/model_data/%s/%s", folders, filenames)


# Parse zee data --------------------------------------------------------

file_path <- "G:/My Drive/BerglundResearch/model_data/data.csv"

for (i in folders) {
  for (j in filenames) {
    file <- sprintf("G:/My Drive/BerglundResearch/model_data/%s/%s", i, j)
    stress <- read_tsv(file)
    new.df <- find.bp(stress, window = 120, i, j)
    if(file.exists(file_path)){
      write_csv(new.df, file_path, append=TRUE, col_names=FALSE)
    } else {
      write_csv(new.df, file_path, append=TRUE, col_names=TRUE)
    }
  }
}
