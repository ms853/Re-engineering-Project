for file in `find . -name '*.java'`
do
    total=$(grep "\/\*" $file | wc -l)
    echo "$file,$total"
done
