library(ggplot2)
data <- read.csv("../../../outputs/Area.csv")
View(data)

#Just show a spread of methods with respect to instruction count and Cyclomatic complexity
ggplot(data,aes(x=Nodes,y=Cyclomatic.Complexity)) + geom_point()

#Add some labels for nodes with higher values of cyclomatic complexity.
ggplot(data,aes(x=Nodes,y=Cyclomatic.Complexity)) + geom_point() + 
  geom_text(data=data[data$Cyclomatic.Complexity>5,],aes(label=Method),nudge_x = 60)