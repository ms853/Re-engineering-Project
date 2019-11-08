package org.assertj.core.internal;

import java.util.List;

//This class is will be used by the DeepDifference class method determine difference, to perform a 'deep' comparison,
//-whereby it traverses through the object graph and does a field by field comparison.  
public class Difference {
	List<String> path;
    Object actual;
    Object other;

    public Difference(List<String> path, Object actual, Object other) {
      this.path = path;
      this.actual = actual;
      this.other = other;
    }

    public List<String> getPath() {
      return path;
    }

    public Object getActual() {
      return actual;
    }

    public Object getOther() {
      return other;
    }

    @Override
    public String toString() {
      return "Difference [path=" + path + ", actual=" + actual + ", other=" + other + "]";
    }
}
