#Iterate through the files and calculate the total number of lines
#for all java files in a given directory.
#Then write the output to a csv file.

echo "Class Name,Lines Of Code" > "../../../../../../../dataFiles/LinesOfCode.csv" #write headers
for file in `find . -name '*.java'`
do
	line=$(wc -l < $file)
    comment1=$(grep "\/\*" $file | wc -l)
    comment2=$(grep "\*" $file | wc -l)
    comment=$(($comment1 + $comment2))
    total=$(($line - $comment))
    echo "$file,$total"
    
    echo "$file,$total" >> "../../../../../../../dataFiles/LinesOfCode.csv" #write to csv.
done

