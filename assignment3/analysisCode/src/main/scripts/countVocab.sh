for file in `find . -name '*.java'`
do
    total=$(grep -roE '\w+' $file | sort -u -f | wc -l)
    echo "$file,$total"
done
