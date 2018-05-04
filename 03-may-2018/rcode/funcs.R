
str.num <- function(string, nums) {
  strings <- character()
  for (i in c(1:length(nums))) {
    strings[[i]] <- sprintf(string, nums[i])
  }
  return(strings)
}

file.list <- function(string, folder.num, file.num) {
  files <- character()
  a <- 0
  for (i in folders) {
    for (j in filenames) {
      a <- a + 1
      files[[a]] <- sprintf(string, i, j)
    }
  }
  return(files)
}

find.bp <- function(stress, window, shift, filename) {
  decomp.storage <- na.omit(decompose(ts(stress$storage, frequency = 12))$trend)
  decomp.population <- na.omit(decompose(ts(stress$population, frequency = 12))$trend)
  bp.storage <- breakpoints(decomp.storage ~ 1, h = window)
  bp.fitted <- fitted(bp.storage, breaks = length(bp.storage$breakpoints))
  stor.fitp <- bp.fitted[c(1, bp.storage$breakpoints, length(bp.fitted))] / 1000
  pop.fitp <- decomp.population[c(1, bp.storage$breakpoints, length(bp.fitted))] / 100000
  bps <- c(0:(length(stor.fitp) - 1))
  timepoint <- c(1, bp.storage$breakpoints, length(bp.fitted))
  data.df <- tbl_df(data.frame(shift, filename, bps, timepoint, stor.fitp, pop.fitp,
                               stringsAsFactors = FALSE))
  return(data.df)
}


make_graph <- function(data, population, window, folder, filename, info) {
  if (is.ts(data)) {
    break
  } else {
    data <- ts(data, frequency=12)
  }
  stor <- (na.omit(ts(decompose(data)$trend, frequency=12)) / 1000)
  trend_fit <- lm(stor ~ 1)
  bp.storage <- breakpoints(stor ~ 1, h=window)
  
  par(mar=c(5,5,4,5))
  plot(stor, ylab='Storage Volume (1000 m3)', ylim=c(0, 150))
  lines(fitted(bp.storage, breaks=length(bp.storage$breakpoints)), col='blue')
  lines(confint(bp.storage, breaks=length(bp.storage$breakpoints)))

  par(new=T)
  plot((population / 100000), axes=F, xlab=NA, ylab=NA, col='red', type='l')
  axis(side=4)
  mtext(side=4, line=3, 'Population (100,000)')

  legend("topright",
         legend=c("Storage", "Storage fit", "Population"),
         lty=c(1,1,1), col=c("black", "blue", "red"))
  title(paste(folder, "/", filename,"\n", info))
  
  return(bp.storage)
}

make_graph2 <- function(data, window, filename, info) {
  
  stor <- (na.omit(decompose(ts(data, frequency=365))$trend))
  trend_fit <- lm(stor ~ 1)
  bp.storage <- breakpoints(stor ~ 1, h=window)

  plot(stor)
  lines(fitted(bp.storage, breaks=length(bp.storage$breakpoints)), col='blue')
  lines(confint(bp.storage, breaks=length(bp.storage$breakpoints)))


#  legend("topright",
#         legend=c("Storage", "Storage fit", "Population"),
#         lty=c(1,1,1), col=c("black", "blue", "red"))
  title(paste(filename, "\n", info))
  
  return(bp.storage)
}