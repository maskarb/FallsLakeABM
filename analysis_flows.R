args = commandArgs(trailingOnly=TRUE)

source("historical_data.R")
library(VineCopula)

vals <- list(fJan, fFeb, fMar, fApr, fMay, fJun, fJul, fAug, fSep, fOct, fNov, fDec)

set.seed(args[1])

cops <- c()
for (i in 1:(length(vals) - 1)) {
  u <- pobs(as.matrix(cbind(vals[[i]], vals[[i + 1]])))[, 1]
  v <- pobs(as.matrix(cbind(vals[[i]], vals[[i + 1]])))[, 2]
  selectedCopula <- BiCopSelect(u, v, familyset = NA, indeptest = TRUE)
  cops <- c(cops, list(selectedCopula))
  if (i == 11) {
    u <- pobs(as.matrix(cbind(vals[[12]], vals[[1]])))[, 1]
    v <- pobs(as.matrix(cbind(vals[[12]], vals[[1]])))[, 2]
    selectedCopula <- BiCopSelect(u, v, familyset = NA, indeptest = TRUE)
    cop <-
      BiCop(selectedCopula$family,
                            selectedCopula$par,
                            selectedCopula$par2)
    cops <- c(list(selectedCopula), cops)
  }
}

u1 <- runif(1)
probs <- c(u1)
for (i in c(2:600)) {
  if (i == 2) {
    u_i_prev <- u1
    cat(paste(toString(u1),"\n"))
  }
  v <- runif(1)
  j <- if (i %% 12 != 0) i %% 12 else 12
  u_i <- BiCopHinv1(u_i_prev, v, obj = cops[[j]])
  cat(paste(toString(u_i),"\n"))
  probs <- c(probs, u_i)
  u_i_prev <- u_i
}
filename <- paste("probs-", toString(args[2]), "-", toString(args[3]), ".csv", sep="")
write.table(probs, file=filename, row.names=FALSE, col.names=FALSE, sep=",")
