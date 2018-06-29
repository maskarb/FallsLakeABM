library(strucchange)
library(tidyverse)
library(lubridate)

num_shift <- c(1:10) / 10
num_run   <- c(0:29)

#num_shift <- 0.1
#num_run   <- 0

date <- "20-jun-2018"

for (i in c(1:length(num_shift))) {
  
  for (j in c(1:length(num_run))) {
    wssp <- 45000
    lake_vol <- 131395

    final.df <- tibble(index=c(1:600))

    filename <- sprintf("reservoir-shift_%.1f-ts-%d.csv", num_shift[i], num_run[j])
    file <- sprintf("~/MASON/%s/%s", date, filename)
    stress <- read_csv(file)
    stress_ts <- ts(stress, frequency=12, class="ts")

    dates             <-  stress_ts[, "dates"      ]
    population        <-  stress_ts[, "population" ] / 100000
    shiftFactor       <-  stress_ts[, "shiftFactor"]
    storage_asis      <-  stress_ts[, "storage"    ]
    storage_asis_perc <- (stress_ts[, "storage"    ] - (lake_vol - wssp)) / wssp * 100

    inflow_ts    <- stress_ts[, "observedInflow"]
    storage_ts   <- ((stress_ts[, "storage"] - (lake_vol - wssp)) / wssp * 100)
    outflow_ts   <- stress_ts[, "outflow"]
    supply_ts    <- stress_ts[, "totalWaterSupply"]
    elevation_ts <- stress_ts[, "elevation"] - 251.5

    inflow_stl    <- stl(inflow_ts   , s.window="periodic")$time.series[,2]
    inflow_stl_t  <- stl(inflow_ts   , t.window=1, s.window="periodic")$time.series[,2]

    storage_stl   <- stl(storage_ts  , s.window="periodic")$time.series[,2]
    outflow_stl   <- stl(outflow_ts  , s.window="periodic")$time.series[,2]
    supply_stl    <- stl(supply_ts   , s.window="periodic")$time.series[,2]
    elevation_stl <- stl(elevation_ts, s.window="periodic")$time.series[,2]

    inflow_trend    <- (decompose(inflow_ts   )$trend)
    storage_trend   <- (decompose(storage_ts  )$trend)
    outflow_trend   <- (decompose(outflow_ts  )$trend)
    supply_trend    <- (decompose(supply_ts   )$trend)
    elevation_trend <- (decompose(elevation_ts)$trend)

    window <- 120
    inflow_bp    <- breakpoints(inflow_stl    ~ 1, h=window)
    storage_bp   <- breakpoints(storage_stl   ~ 1, h=window)
    outflow_bp   <- breakpoints(outflow_stl   ~ 1, h=window)
    supply_bp    <- breakpoints(supply_stl    ~ 1, h=window)
    elevation_bp <- breakpoints(elevation_stl ~ 1, h=window)

    inflow_fac    <- breakfactor(inflow_bp   )
    storage_fac   <- breakfactor(storage_bp  )
    outflow_fac   <- breakfactor(outflow_bp  )
    supply_fac    <- breakfactor(supply_bp   )
    elevation_fac <- breakfactor(elevation_bp)

    inflow_segment    <- fitted(inflow_bp   , breaks=length(inflow_bp   $breakpoints))
    storage_segment   <- fitted(storage_bp  , breaks=length(storage_bp  $breakpoints))
    outflow_segment   <- fitted(outflow_bp  , breaks=length(outflow_bp  $breakpoints))
    supply_segment    <- fitted(supply_bp   , breaks=length(supply_bp   $breakpoints))
    elevation_segment <- fitted(elevation_bp, breaks=length(elevation_bp$breakpoints))

    inflow    <- tibble(inflow_ts, inflow_trend, inflow_stl, inflow_fac, inflow_segment)
    storage   <- tibble(storage_ts, storage_trend, storage_stl, storage_fac, storage_segment)
    outflow   <- tibble(outflow_ts, outflow_trend, outflow_stl, outflow_fac, outflow_segment)
    supply    <- tibble(supply_ts, supply_trend, supply_stl, supply_fac, supply_segment)
    elevation <- tibble(elevation_ts, elevation_trend, elevation_stl, elevation_fac, elevation_segment)

    final.df <- bind_cols(final.df, tibble("dates"=dates),
                            tibble("shiftFactor"=shiftFactor),
                            tibble("population"=population),
                            tibble("storage"=storage_asis),
                            tibble("storage_perc"=storage_asis_perc),
                            storage, inflow, outflow, supply, elevation)

    file_path <- sprintf("~/MASON/%s/reservoir-shift_%.1f-ts-%d_%s.csv", date, num_shift[i], num_run[j], "breakpoints")
    write_csv(final.df, file_path, append = FALSE, col_names=TRUE)
  }
}