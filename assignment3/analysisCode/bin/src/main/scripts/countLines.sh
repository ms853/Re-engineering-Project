for file in `find . -name '*.java'`
do
    total=$(wc -l < $file)
    echo "$file,$total"
done
