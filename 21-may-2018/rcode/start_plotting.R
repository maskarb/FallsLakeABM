library(plotly)
library(tidyverse)

data.df <- read_csv(sprintf("G:/My Drive/BerglundResearch/model_data/data3new.csv"))%>%
  filter(timepoint != 588, bps != 0)
early_timepoint <- filter(data.df, timepoint == 182)



x <- data.df$pop.fitp
y <- data.df$stor.fitp

ggplot(data.df, aes(pop.fitp, stor.fitp)) +
  geom_point(data=data.df, aes(pop.fitp, stor.fitp, color=filename)) +
  stat_summary(fun.data=mean_cl_normal) +
  geom_smooth(method='lm', se=FALSE, color='black') +
  facet_wrap(~ shift, nrow=2)

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
