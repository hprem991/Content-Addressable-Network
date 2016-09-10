JFLAGS = -g
JC = javac
#
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
CLASSES = Boot.java Content.java Leave.java Node.java Peer.java Search.java View.java Zone.java
default: classes
classes: $(CLASSES:.java=.class)
clean:
	$(RM) *.class
