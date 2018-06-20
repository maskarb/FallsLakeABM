cbind.fill <- function(...){
    nm <- list(...) 
    nm <- lapply(nm, as.matrix)
    n <- max(sapply(nm, nrow)) 
    do.call(cbind, lapply(nm, function (x) 
        rbind(x, matrix(, n-nrow(x), ncol(x))))) 
}

str.num1 <- function(string, nums) {
  strings <- character()
  for (i in c(1:length(nums))) {
    strings[[i]] <- sprintf(string, nums[i])
  }
  return(strings)
}

str.num2 <- function(string, nums1, nums2) {
  strings <- matrix(data = NA, nrow = length(nums1), ncol = length(nums2))
  for (i in c(1:length(nums1))) {
    for (j in c(1:length(nums2))) {
      strings[[i, j]] <- sprintf(string, nums1[i], nums2[j])
    }
  }
  return(strings)
}

file.list <- function(string, file.num) {
  files <- character()
  a <- 0
  for (i in filenames) {
    a <- a + 1
    files[[a]] <- sprintf(string, i)
    }
  return(files)
}

find.bp <- function(stress, window, shift, filename) {
  decomp.storage <- stl(ts(stress$storage, frequency=12), s.window="periodic")$time.series[,2] / 131395 * 100
  population <- ts(stress$population, frequency=12)
  shiftFactor <- ts(stress$shiftFactor, frequency=12)*100
  bp.storage <- breakpoints(decomp.storage ~ 1, h = window)
  bp.fitted <- fitted(bp.strp, breaks=length(bp.strp$breakpoints))
  stor.fitp <- bp.fitted[c(1, bp.storage$breakpoints, length(bp.fitted))]
  pop.fitp <- population[c(1, bp.storage$breakpoints, length(bp.fitted))] / 100000
  shift.fitp <- shiftFactor[c(1, bp.storage$breakpoints, length(bp.fitted))]
  bps <- c(0:(length(stor.fitp) - 1))
  timepoint <- c(1, bp.storage$breakpoints, length(bp.fitted))
  data.df <- tbl_df(data.frame(shift, filename, bps, timepoint, 
                                pop.fitp, shift.fitp, stor.fitp, 
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

  l <- length(storage)
  tt <- 1:l

  bp.storage <- breakpoints(stor ~ 1, h=window)
  bp.inflow  <- breakpoints(infl ~ 1, h=window)
  bp.supply  <- breakpoints(supp ~ tt, h=window)
  bp.all_vars<- breakpoints(all_vars ~ 1, h=window)
  
  par(mar=c(5,5,4,5))
  plot(stor, ylab='Storage Volume (% Full)', ylim=c(0, max(stor)))
  lines(fitted(bp.storage, breaks=length(bp.storage$breakpoints)), col='blue')
  lines(confint(bp.storage, breaks=length(bp.storage$breakpoints)))


  par(new=T)
  var <- all_vars
  bp.var <- bp.all_vars
  plot(var, axes=F, xlab=NA, ylab=NA, col='red', type='l', ylim=c(0, max(var)))
  lines(fitted(bp.var, breaks=length(bp.var$breakpoints)), col='orange')
  lines(confint(bp.var, breaks=length(bp.var$breakpoints)))
  axis(side=4)
  mtext(side=4, line=3, 'Outflow (acre-feet)')

  par(new=T)
  plot((population / 100000), axes=F, xlab=NA, ylab=NA, col='red', type='l')
  axis(side=4)
  mtext(side=4, line=3, 'Population (100,000)')

  legend("topleft",
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

create_very_specific_graph <- function(num.folder, num.filename, stor, bp.storage,
                                        population, shiftFactor, folder, filename) {
  pdf(sprintf("~/MASON/21-may-2018/%.1f-ts-%d.pdf", num.folder, num.filename))
  par(mar=c(5,5,4,5))
  plot(stor, ylab='Storage Volume (% full pool)', ylim=c(0, 150))
  lines(shiftFactor, lty='dotted', col='gray')
  lines(fitted(bp.storage, breaks=length(bp.storage$breakpoints)), col='blue')
  lines(confint(bp.storage, breaks=length(bp.storage$breakpoints)))
  
  par(new=T)
  plot((population / 100000), axes=F, xlab=NA, ylab=NA, col='green3', type='l')
  axis(side=4)
  mtext(side=4, line=3, 'Population (100,000)')
  
  legend("topright",
         legend=c("Storage", "Storage fit", "Population"),
         lty=c(1,1,1), col=c("black", "blue", "green3"))
  title(paste(folder, "/", filename,"\n"))
  dev.off()
}