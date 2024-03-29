import pyrebase
from subprocess import Popen, PIPE
import os
import plot

# TODO: Hide API KEYS
config = {
            "apiKey": "AIzaSyCJpCjOic_GUuopX-GV3NbI4Kpt-1DV-Ss",
            "authDomain": "iguardindia-1adf8.firebaseapp.com",
            "databaseURL": "https://iguardindia-1adf8.firebaseio.com/",
            "storageBucket": "cognitive-recommender.appspot.com"
         }

# Connecting to Firebase
Firebase = pyrebase.initialize_app(config)

# Connects to Firebase Database
Database = Firebase.database()

Username = input("Enter the username: ")

while(1):
    # Pulling data from the Firebase Database
    Data = Database.child(Username).get()

    # Dictionary to store the results of the images captured by the user
    AIResults = {}

    # Extracting the results from JSON Format
    NumImages = int(Data.val()["CurrentImage"])
    for i in range(1, NumImages + 1):
        AIResults[i] = Data.val()[str(i)] # Stores the predictions from each image

    # Visualizing the Results
    plot.viz(AIResults, NumImages)

    # Asking for updation
    inp = input("\nEnter 'Y/y' to update: ")
    print("___________________________________________________________________________________________________________________________________________________________________________________________________________")
    os.system("clear") # Clear the screen before the update

    # Break
    if not(inp == "Y" or inp == "y"):
        break
