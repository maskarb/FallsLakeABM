# used to convert the elevations.txt to a dictionary for use in python
with open('els.txt', 'r') as f:
    diction = {}
    lines = f.readlines()
    for line in lines:
        temp = line.strip()
        temp = temp.split('\t')
        if eval(temp[0]) < 244.5:
            diction[temp[0]] = [eval(temp[1]), eval(temp[2])]
    print(diction)
