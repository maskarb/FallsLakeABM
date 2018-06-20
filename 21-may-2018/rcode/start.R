library(strucchange)
library(tidyverse)
source('~/MASON/21-may-2018/rcode/funcs.R')

num.folder <- c(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0)
num.filename <- c(0:9)
num.filename <- 9
num.folder <- 0.7
#filenames <- str.num2("reservoir-shift_%.1f-ts-%d.txt", num.folder, num.filename)
#folders <- str.num1("%.1f_shift", num.folder)
i <- 1
j <- 1
for (i in c(1:length(num.folder))) {
  folder <- sprintf("%.1f_shift", num.folder[i])
  for (j in c(1:length(num.filename))) {
    filename <- sprintf("reservoir-shift_%.1f-ts-%d.txt", num.folder[i], num.filename[j])
    
    file <- sprintf("~/MASON/21-may-2018/%s", filename)
    stress <- read_delim(file, ' ')
    stress_ts <- ts(stress, frequency=12, class="ts")

    inflow      <- stress_ts[, "observedInflow"]
    outflow     <- stress_ts[, "outflow"]
    supply      <- stress_ts[, "waterSupply"]
    storage_per <- stress_ts[, "storage"] / 131395 * 100 # percent of full
    storage_vol <- stress_ts[, "storage"]                # acre-feet
    population  <- stress_ts[, "population"] / 100000
    shiftFactor <- stress_ts[, "shiftFactor"] * 100
    elevation   <- stress_ts[, "elevation"] - 251.5

    infl <- stl(inflow, s.window="periodic")$time.series[,2]
    outf <- stl(outflow, s.window="periodic")$time.series[,2]
    supp <- stl(supply, s.window="periodic")$time.series[,2]
    strp <- stl(storage_per, s.window="periodic")$time.series[,2]
    strv <- stl(storage_vol, s.window="periodic")$time.series[,2]
#    popu <- stl(population, s.window="periodic")$time.series[,2] # do not decompose
#    shif <- stl(shiftFactor, s.window="periodic")$time.series[,2] # do not decopose
    elev <- stl(elevation, s.window="periodic")$time.series[,2]
#    stor <- StructTS(storage, type = c("BSM"))$fitted[,1] # could be another method worth looking into.

    window <- 120
    infl.bp <- breakpoints(infl ~ 1, h=window)
    outf.bp <- breakpoints(outf ~ 1, h=window)
    supp.bp <- breakpoints(supp ~ 1, h=window)
    strp.bp <- breakpoints(strp ~ 1, h=window)
    strv.bp <- breakpoints(strv ~ 1, h=window)
#    bp.popu <- breakpoints(popu ~ 1, h=window) # what's the point? this is explicitly linear
#    bp.shif <- breakpoints(shif ~ 1, h=window) # what's the point? this is explicitly linear
    elev.bp <- breakpoints(elev ~ 1, h=window)

    infl.numbp <- c(1:(length(infl.bp$breakpoints)+1))
    outf.numbp <- c(1:(length(outf.bp$breakpoints)+1))
    supp.numbp <- c(1:(length(supp.bp$breakpoints)+1))
    strp.numbp <- c(1:(length(strp.bp$breakpoints)+1))
    strv.numbp <- c(1:(length(strv.bp$breakpoints)+1))
    elev.numbp <- c(1:(length(elev.bp$breakpoints)+1))

    infl.bp.fitted <- fitted(infl.bp, breaks=length(infl.bp$breakpoints))
    outf.bp.fitted <- fitted(outf.bp, breaks=length(outf.bp$breakpoints))
    supp.bp.fitted <- fitted(supp.bp, breaks=length(supp.bp$breakpoints))
    strp.bp.fitted <- fitted(strp.bp, breaks=length(strp.bp$breakpoints))
    strv.bp.fitted <- fitted(strv.bp, breaks=length(strv.bp$breakpoints))
    elev.bp.fitted <- fitted(elev.bp, breaks=length(elev.bp$breakpoints))

    infl.fitp <- infl.bp.fitted[c(infl.bp$breakpoints, tail(infl.bp$breakpoints, n=1)+1)]
    outf.fitp <- outf.bp.fitted[c(outf.bp$breakpoints, tail(outf.bp$breakpoints, n=1)+1)]
    supp.fitp <- supp.bp.fitted[c(supp.bp$breakpoints, tail(supp.bp$breakpoints, n=1)+1)]
    strp.fitp <- strp.bp.fitted[c(strp.bp$breakpoints, tail(strp.bp$breakpoints, n=1)+1)]
    strv.fitp <- strv.bp.fitted[c(strv.bp$breakpoints, tail(strv.bp$breakpoints, n=1)+1)]
    elev.fitp <- elev.bp.fitted[c(elev.bp$breakpoints, tail(elev.bp$breakpoints, n=1)+1)]

    infl.pop.fitp <- population[c(infl.bp$breakpoints, length(population))]
    outf.pop.fitp <- population[c(outf.bp$breakpoints, length(population))]
    supp.pop.fitp <- population[c(supp.bp$breakpoints, length(population))]
    strp.pop.fitp <- population[c(strp.bp$breakpoints, length(population))]
    strv.pop.fitp <- population[c(strv.bp$breakpoints, length(population))]
    elev.pop.fitp <- population[c(elev.bp$breakpoints, length(population))]

    infl.shif.fitp <- shiftFactor[c(infl.bp$breakpoints, length(shiftFactor))]
    outf.shif.fitp <- shiftFactor[c(outf.bp$breakpoints, length(shiftFactor))]
    supp.shif.fitp <- shiftFactor[c(supp.bp$breakpoints, length(shiftFactor))]
    strp.shif.fitp <- shiftFactor[c(strp.bp$breakpoints, length(shiftFactor))]
    strv.shif.fitp <- shiftFactor[c(strv.bp$breakpoints, length(shiftFactor))]
    elev.shif.fitp <- shiftFactor[c(elev.bp$breakpoints, length(shiftFactor))]

    df.infl <- data.frame(infl.numbp, infl.fitp, infl.pop.fitp, infl.shif.fitp, stringsAsFactors = FALSE)
    df.outf <- data.frame(outf.numbp, outf.fitp, outf.pop.fitp, outf.shif.fitp, stringsAsFactors = FALSE)
    df.supp <- data.frame(supp.numbp, supp.fitp, supp.pop.fitp, supp.shif.fitp, stringsAsFactors = FALSE)
    df.strp <- data.frame(strp.numbp, strp.fitp, strp.pop.fitp, strp.shif.fitp, stringsAsFactors = FALSE)
    df.strv <- data.frame(strv.numbp, strv.fitp, strv.pop.fitp, strv.shif.fitp, stringsAsFactors = FALSE)
    df.elev <- data.frame(elev.numbp, elev.fitp, elev.pop.fitp, elev.shif.fitp, stringsAsFactors = FALSE)

    data.df <- data.frame(folder, filename, cbind.fill(df.infl, df.outf, df.supp, df.strp, df.strv, df.elev))


#    create_very_specific_graph(num.folder[i], num.filename[j], stor, bp.storage,
#                                        population, shiftFactor, folder, filename)



    file_path <- sprintf("~/MASON/21-may-2018/%s.csv", folder)
    if(file.exists(file_path)){
      write_csv(data.df, file_path, append=TRUE, col_names=FALSE)
    } else {
      write_csv(data.df, file_path, append=TRUE, col_names=TRUE)
    }
  }
}