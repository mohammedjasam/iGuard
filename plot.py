import matplotlib.pyplot as plt

# Function to visualize the data
def viz(Results, NumImages):
    # Extracting the number of images for the X-Axis
    x = [i + 1 for i in range(NumImages)]

    # Dictionary to store results of individual emotions
    Set = {}

    # Separating and accumulating emotions
    for i in range(1, NumImages + 1):
        temp = Results[i]
        tempValues = []
        for k, v in temp.items():
            if k in Set:
                Set[k] = Set[k] + [v]
            else:
                Set[k] = [v]

    # Plotting the Emotion Scores
    for k, v in Set.items():
        plt.plot(x, v)

    # Naming the lines
    plt.gca().legend(('Anger', 'Disgust', 'Fear', 'Happiness', 'Sadness', 'Surprise'))

    plt.title('Visualizing Emotions over a period of Time')
    plt.xlabel('Probability of Emotions')
    plt.ylabel('Images Numbers')
    # Displaying the Graph
    plt.show()
