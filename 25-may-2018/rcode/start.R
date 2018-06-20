.libPaths('C:\\R\\library')
library(strucchange)
library(tidyverse)
library(lubridate)
source('~/MASON/25-may-2018/rcode/funcs.R')

num.folder <- c(1:10) / 10
num.filename <- c(0:9)
num.filename <- 9
num.folder <- 0.7

# params <- c("infl", "outf", "supp", "strp", "strv", "elev")
params <- c("observedInflow", "storage", "outflow", "waterSupply", "elevation")

#filenames <- str.num2("reservoir-shift_%.1f-ts-%d.txt", num.folder, num.filename)
#folders <- str.num1("%.1f_shift", num.folder)
i <- 1
j <- 1
for (i in c(1:length(num.folder))) {
  folder <- sprintf("%.1f_shift", num.folder[i])
  
  for (j in c(1:length(num.filename))) {
    wssp <- 45000
    lake_vol <- 131395

    final.df <- tibble(index=c(1:600))

    filename <- sprintf("reservoir-shift_%.1f-ts-%d.txt", num.folder[i], num.filename[j])
    file <- sprintf("~/MASON/25-may-2018/%s", filename)
    stress <- read_delim(file, ' ')
    stress_ts <- ts(stress, frequency=12, class="ts")

    population  <- stress_ts[, "population"    ] / 100000
    shiftFactor <- stress_ts[, "shiftFactor"   ]
    storage_asis<- (stress_ts[, "storage"      ] - (lake_vol - wssp)) / wssp * 100
    dates <- month(seq(ymd('2013-01-01'), ymd('2062-12-01'), by='months'))

    final.df <- bind_cols(final.df, tibble("dates"=dates), 
                            tibble("shiftFactor"=shiftFactor), 
                            tibble("population"=population),
                            tibble("storage_asis"=storage_asis))

#    stor <- StructTS(storage, type = c("BSM"))$fitted[,1] # could be another method worth looking into.

    window <- 120

#    create_very_specific_graph(num.folder[i], num.filename[j], stor, bp.storage,
#                                        population, shiftFactor, folder, filename)

    for (param in params) {
        if (param != "storage" && param != "elevation") {
            parameter <- stress_ts[, param]
        } else if (param == "storage") {
            parameter <- ((stress_ts[, param] - (lake_vol - wssp)) / wssp * 100)
        } else {
            parameter <- stress_ts[, param] - 251.5
        }
        para <- stl(parameter, s.window="periodic")$time.series[,2]
        para.bp <- breakpoints(para ~ 1, h=window)
        para.numbp <- c(1:(length(para.bp$breakpoints)+1))
        para.bp.fitted <- fitted(para.bp, breaks=length(para.bp$breakpoints))
#        para.fitp <- para.bp.fitted[c(para.bp$breakpoints, tail(para.bp$breakpoints, n=1)+1)]
#        para.pop.fitp <- population[c(para.bp$breakpoints, length(population))]
#        para.shif.fitp <- shiftFactor[c(para.bp$breakpoints, length(shiftFactor))]
#        para.df <- data.frame(para.numbp, para.fitp, para.pop.fitp, para.shif.fitp, stringsAsFactors = FALSE)

        col_1 = sprintf("%s", param)
        col_2 = sprintf("%s_fitted",param)

        para.df <- tibble(!!col_1:=para, !!col_2:=para.bp.fitted) # holy fuck this is annoying ass syntax.

        final.df <- bind_cols(final.df, para.df)
    }

    file_path <- sprintf("~/MASON/25-may-2018/%s_ts-%s_%s.csv", folder, num.filename[j], "all_params")
    if(file.exists(file_path)){
    write_csv(final.df, file_path, append=TRUE, col_names=FALSE)
    } else {
    write_csv(final.df, file_path, append=TRUE, col_names=TRUE)
    }
  }
}


i <- 1
j <- 1

folder <- sprintf("%.1f_shift", num.folder[i])

filename <- sprintf("reservoir-shift_%.1f-ts-%d.txt", num.folder[i], num.filename[j])
file <- sprintf("~/MASON/25-may-2018/%s", filename)

stress <- read_delim(file, ' ')
stress_ts <- ts(stress, start=2013, frequency=12, class="ts")

population  <- stress_ts[, "population"    ] / 100000
shiftFactor <- stress_ts[, "shiftFactor"   ]

window <- 120

parameter <- (stress_ts[, "storage"] / 131395 * 100)
para <- (stl(parameter, s.window="periodic")$time.series[,2])
para.bp <- breakpoints(para ~ 1, h=window)
para.numbp <- c(1:(length(para.bp$breakpoints)+1))
para.bp.fitted <- fitted(para.bp, breaks=length(para.bp$breakpoints))
para.fitp <- para.bp.fitted[c(para.bp$breakpoints, tail(para.bp$breakpoints, n=1)+1)]
para.pop.fitp <- population[c(para.bp$breakpoints, length(population))]
para.shif.fitp <- shiftFactor[c(para.bp$breakpoints, length(shiftFactor))]
para.df <- data.frame(para.numbp, para.fitp, para.pop.fitp, para.shif.fitp, stringsAsFactors = FALSE)


p <- ggplot(para) +
        geom_line()
p

par(mar=c(5,5,4,5))
plot(para, ylab='Storage Volume (% Full)')
lines(fitted(para.bp, breaks=length(para.bp$breakpoints)), col='blue')
lines(confint(para.bp, breaks=length(para.bp$breakpoints)))
par(new=T)

plot(var, axes=F, xlab=NA, ylab=NA, col='red', type='l', ylim=c(0, max(var)))
lines(fitted(bp.var, breaks=length(bp.var$breakpoints)), col='orange')
lines(confint(bp.var, breaks=length(bp.var$breakpoints)))
axis(side=4)
mtext(side=4, line=3, 'Outflow (acre-feet)')

