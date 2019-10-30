# DigitNet
This project conststs of a framework for generating fully-connected feed-forward neural networks. I coded these from scratch, along with all of its associated algorithms, in Java. The project includes test cases of these networks on the mnist dataset.

### Testing out a previously-trained network
To test out a previously-trained network, run the class "display/DrawTester.java"

##### Note
Testing the network this way will cause performance to be much worse than testing it directly with the Mnist test set, since when drawing the numbers by hand in the DrawTester frame, the numbers can easily vary considerably from the Mnist format (i. e. not being centered in the frame, being thicker or thinner, being denser or less dense). A more accurate reflextion of the network's success and error rates could be found by testing it directly with the Mnist test set (which wasn't used in training). I just thought drawing numbers by hand would be a more fun way to test it out, even though it didn't end up working as well as planned.

### Licensing
Everything in this project is my work, except for the files in the mnist package, which were released fair use online for extracting data from the mnist dataset. I slightly modified some of these files for use in this project.
