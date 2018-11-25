import os
os.chdir('src/pertubing')


from lookup_elev import els_stor
from lookup_stor import stor_els
from lookup_area import els_area


def release(storage, month: int, days: int):
    elevation = lookup(storage, stor_els)
    normalElavation = 0
    release = 0
    clayton = 6000
    liftedAmount = 2000
    maxDischarge = 8000 # (cfs) approximate at elevation 250 m.s.l.
    lakeLevelForecast = 268
    freeOverflowing = lookup(elevation, els_stor) - lookup(268, els_stor)
    uncontroledDrainageAreaFlow = 4000

    minimumReleaseSummer = 100
    minimumReleaseWinter = 60

    counter = 0
    totalRelease = 0
    if (month >= 4 and month <= 8):
        release = minimumReleaseSummer
        normalElavation = 251.5
        if (elevation <= normalElavation):
            storage -= release * 3600 * 24 * days / 43560
            totalRelease = release * 3600 * 24 * days / 43560
        else:
            while (elevation > normalElavation and counter < days):
                if (elevation <= 255):
                    if (clayton <= 7000):
                        release = min(7000 - uncontroledDrainageAreaFlow, 4000)
                elif (elevation <= 258):
                    if (clayton <= 7000):
                        release = min(7000 - uncontroledDrainageAreaFlow, 4000 + liftedAmount)
                elif (elevation <= 264):
                    if (clayton <= 8000):
                        release = min(8000 - uncontroledDrainageAreaFlow, 4000 + liftedAmount)
                elif (elevation <= 268):
                    if (lakeLevelForecast <= 268 and clayton >= 8000):
                        release = freeOverflowing
                    else:
                        release = maxDischarge
                else:
                    release = maxDischarge
                storage -= (release * 3600 * 24) / 43560
                elevation = lookup(storage, stor_els)
                counter += 1
                totalRelease += (release * 3600 * 24 / 43560)
    elif (month >= 9 or month <= 3):
        release = minimumReleaseWinter
        normalElavation = 250.1
        if (elevation <= normalElavation):
            storage -= release * 3600 * 24 * days / 43560
            totalRelease += (release * 3600 * 24 * days / 43560)
        else:
            while (elevation > normalElavation and counter < days):
                if (elevation <= 255):
                    if (clayton <= 7000):
                        release = min(7000 - clayton, 4000)
                elif (elevation <= 258):
                    if (clayton <= 7000):
                        release = min(7000 - clayton, 4000 + liftedAmount)
                elif (elevation <= 264):
                    if (clayton <= 8000):
                        release = min(8000 - clayton, 4000 + liftedAmount)
                elif (elevation <= 268):
                    if (lakeLevelForecast <= 268 and clayton >= 8000):
                        release = freeOverflowing
                    else:
                        release = maxDischarge
                else:
                    release = maxDischarge

                storage -= release * 3600 * 24 / 43560
                elevation = lookup(storage, stor_els)
                counter += 1
                totalRelease += (release * 3600 * 24 / 43560)
    outflow = totalRelease


def days_in_month(month: int) -> int:
    if month == 2:
        days = 28
    elif month in [4, 6, 9, 11]:
        days = 30
    else:
        days = 31
    return days

def lookup(num, dic):
    x0, y0 = 0, 0
    val = dic.get(num)
    if val is None:
        for k, v in dic.items():
            if num < k:
                x1, y1 = k, v
                return (y1 - y0)/(x1 - x0) * (num - x0) + y0
            x0, y0 = k, v
    else:
        return val

