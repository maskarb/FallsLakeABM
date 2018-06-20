library(plotly)
library(tidyverse)

params <- c("observedInflow", "storage", "outflow", "waterSupply", "elevation")
shifts <- c(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0)

params <- "storage"
shift <- c(0.1)
data.df <- data.frame(stringsAsFactors=FALSE)

for (shift in shifts) {
  for (param in params) {
    file <- sprintf("%0.1f_shift_%s", shift, param)
    if (shift < 1.0) filt <- shift + 0.01 else filt <- shift
    data <- read_csv(sprintf("~/MASON/25-May-2018/%s.csv", file))%>%
      filter(para.shif.fitp >= filt)

    data.df <- rbind(data.df, na.omit(data))

    p <- ggplot(data.df, aes(para.shif.fitp, para.pop.fitp)) +
      geom_point() +
      coord_cartesian(xlim=c(0, 1)) +
      scale_x_reverse()
    p + facet_grid(para.numbp ~ .) + ggtitle(file)
    # ggsave(sprintf("~/MASON/25-may-2018/%s.pdf", file), plot=last_plot())
  }
}


data.df <- read_csv(sprintf("~/MASON/25-May-2018/%0.1f_shift_%s.csv", shift, param))%>%
  filter(para.shif.fitp >= 0.71)
#data.df <- filter(final.df, outf.shif.fitp == 70.05)

data.df <- na.omit(data.df)

p <- ggplot(data.df, aes(para.shif.fitp, para.fitp)) +
  geom_point() +
  coord_cartesian(xlim=c(0, 1)) +
  scale_x_reverse()
p + facet_grid(para.numbp ~ .) + ggtitle("all shifts - 10 runs\nshift factor vs storage (%)")

 # facet_wrap(~ color)

ggplot(data=data.df) +
  geom_point(data=data.df, aes(pop.fitp, stor.fitp,
                          shape=filename,
                          color=filename)) +
  facet_wrap(~ folder, nrow=2)

num.shift <- c(0.70, 0.75, 0.80, 0.85, 0.90, 0.95, 1.00)
equations <- tibble(shift=num.shift, empty=NA)
for (i in c(1:length(num.shift))) {
  equations[i, ] = mutate(equations, intercept = (lm(stor.fitp ~ pop.fitp, 
                                                filter(data.df, shift == num.shift[i]))$coefficients[1]),
                                          slope = (lm(stor.fitp ~ pop.fitp, 
                                                filter(data.df, shift == num.shift[i]))$coefficients[2]))
}
equation <- lm(data.df$stor.fitp ~ data.df$pop.fitp)

facet_wrap(~ folder, nrow=2)

data <- filter(data.df, bps != 588)

p <- plot_ly(data, x=data$pop.fitp, y=data$stor.fitp, 
             color=data$filename)
p
