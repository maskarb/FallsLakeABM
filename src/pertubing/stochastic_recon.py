import random

from statistics import mean, stdev
from unittest import TestCase

import numpy as np

from scipy.stats import norm, pearsonr
from scipy.interpolate import InterpolatedUnivariateSpline

from historical_data import h_data

shift = 0.5

shiftFactor = list(np.arange(1, shift, 600))
probs = list(np.linspace(0, 1, 20))

for i in range(1, 601):
    month = i % 12 # 0 is December
    flows = h_data[month][0]
    precip= h_data[month][1]
    evapor= h_data[month][2]

    precip_corr, __ = pearsonr(flows, precip)
    evapor_corr, __ = pearsonr(flows, evapor)

    if shiftFactor[i-1] < 1:
        precip_shift = 1 - (1 - shiftFactor[i-1]) * precip_corr
        evapor_shift = 1 - (1 - shiftFactor[i-1]) * evapor_corr
    else:
        precip_shift = (shiftFactor[i-1] - 1) * precip_corr + 1
        evapor_shift = (shiftFactor[i-1] - 1) * evapor_corr + 1

    flows_recon = 0
    precip_recon= 0
    evapor_recon= 0


def reconstruct(time, values, shift_factor, probs):
    return

values = sorted(flows)
shift_factor = 0.5
probs = probs

average = mean(values)
std_dev = stdev(values)
new_ave = average * shift_factor
old_dist = norm(average, std_dev)
new_dist = norm(new_ave, std_dev)


y = [0] + values
x = list(range(len(y)))
c_spline = InterpolatedUnivariateSpline(x, y)

new_probs = []
for p in probs:
    temp = new_dist.ppf(p)
    new_probs.append(old_dist.cdf(temp) * len(values))

new_vals = list(c_spline.__call__(new_probs))

x = list(range(len(new_vals)))
n_spline = InterpolatedUnivariateSpline(x, new_vals)

sample = random.uniform(0, 1)
sample = 0.5
val_for_timepoint = float(n_spline.__call__(sample))
