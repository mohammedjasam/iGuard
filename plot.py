

def viz():
    import matplotlib.pyplot as plt
    x = [2, 4, 6, 8, 10, 12, 14, 16]
    y = [1, 3, 5, 7, 11, 13, 15, 17]
    y1 = [1,1,1,1,1,1,1,1]
    y2 = [2, 4, 6, 3, 5, 2, 7, 1]
    plt.plot(x, y, x, y1, x, y2)
    plt.show()
