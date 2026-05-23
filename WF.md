# Interview Questions & Answers

## Question 1: Show Non-Null Contact Information from Student Table

### Problem Statement
You have a Student table with columns: `name`, `email`, and `phone`. In some cases, either `email` or `phone` can be null.

**Task:** Write a query to display the student's name along with whichever contact information is available (email or phone, whichever is not null).

---

### Table Schema

```sql
CREATE TABLE Student (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20)
);
```

**Sample Data:**
| id | name      | email           | phone       |
|----|-----------|-----------------|-------------|
| 1  | John Doe  | john@email.com  | NULL        |
| 2  | Jane Smith| NULL            | 9876543210  |
| 3  | Bob Brown | bob@email.com   | 9123456789  |
| 4  | Alice Lee | NULL            | NULL        |

---

### Solution

#### Approach 1: Using COALESCE() Function (Recommended)
```sql
SELECT 
    name,
    COALESCE(email, phone) AS contact_info
FROM Student;
```

**Explanation:** `COALESCE()` returns the first non-null value from the list of arguments. It will return `email` if it's not null, otherwise it will return `phone`.

---

## Question 2: JVM is going OutOfMemoryError. How will you find which code is causing it?

They usually want to test:

* JVM memory knowledge
* debugging approach
* production troubleshooting
* profiling tools
* heap dump analysis

A strong answer should be step-by-step.

---

### 1. First identify WHICH memory is full

`OutOfMemoryError` can happen in different JVM areas:

```java
java.lang.OutOfMemoryError: Java heap space
java.lang.OutOfMemoryError: Metaspace
java.lang.OutOfMemoryError: GC overhead limit exceeded
java.lang.OutOfMemoryError: Direct buffer memory
```

Each points to different problems.

---

### 2. Enable heap dump on OOM

Start JVM with:

```bash
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/tmp/heapdump.hprof
```

When OOM happens, JVM automatically creates heap dump.

Heap dump = snapshot of all objects in memory.

---

### 3. Analyze heap dump

Use tools like:

* Eclipse Memory Analyzer Tool (MAT)
* VisualVM
* JProfiler
* YourKit

Most common in interviews:
👉 MAT (Memory Analyzer Tool)

---

### 4. What to check in heap dump

#### A. Biggest objects

Check:

* which objects consume most heap
* large collections
* cache growth
* duplicate objects

Example:

```java
List<Order> orders = new ArrayList<>();
```

If list contains 50 million objects,
it may consume GBs of memory.

---

#### B. Dominator Tree

Very important concept.

Shows:

* which object is retaining memory
* why GC cannot free it

Example:

```java
static Map<String, User> cache = new HashMap<>();
```

Static map keeps references forever.

GC cannot remove them.

Because GC removes only objects that are unreachable.

A static variable belongs to the class itself, and the class is usually alive till JVM shutdown.

So objects referenced by a static variable remain reachable.

---

#### C. Leak Suspects Report

MAT automatically generates:

* suspected memory leak
* largest retained objects
* reference chain

---

### 5. Find reference chain

Suppose millions of `User` objects exist.

Need to know:
👉 who is holding them?

Example chain:

```text
Thread
  -> Service
      -> static cache
          -> HashMap
              -> User objects
```

Then you identify exact code.

---
A memory leak means:
Memory is occupied by objects that are no longer needed by the application, but they are still reachable, so GC cannot remove them.

### Tricky cross questions interviewer may ask

#### Q1. Why GC is not freeing memory?

Because objects are still strongly reachable.

---

#### Q2. Difference between memory leak in Java vs C++?

In Java:

* objects are reachable accidentally

In C++:

* memory manually not freed

---

#### Q3. Can Java have memory leak despite GC?

Yes.
GC only removes unreachable objects.

---

#### Q4. What is retained heap?

Memory retained because of an object and everything reachable from it.

---

#### Q5. Why static variables are dangerous?

They live till class unloading/JVM shutdown.

---

#### Q6. How to detect memory leak without heap dump?

* GC logs
* increasing heap usage
* frequent Full GC
* monitoring tools
* object histogram

---

#### Q7. What happens before OOM?

Usually:

* heap fills
* frequent Full GC
* GC tries reclaiming memory
* cannot reclaim enough
* OOM thrown

---

#### Q8. Difference between heap overflow and stack overflow?

Heap:

* too many objects

Stack:

* deep recursion / huge stack frames

---

#### Q9. Which JVM area stores objects?

Primarily Heap.

Class metadata → Metaspace.

Thread-local execution data → Stack.

---

#### Q10. What is GC overhead limit exceeded?

JVM spends too much time doing GC but recovers very little memory.

Usually indicates memory leak.

## Question 3: Write a Singleton design code

A Singleton means:

Only one object of a class exists in the JVM, and the same instance is shared everywhere.

---
public class TVSet {

    private static volatile TVSet tvSetInstance = null;
 
    private TVSet() {
        System.out.println("TV Set Instance created");
    }

    public static TVSet getTvSetInstance() {
        if (tvSetInstance == null) {
            synchronized (TVSet.class) {
                if (tvSetInstance == null) {
                    tvSetInstance = new TVSet();
                }
            }
        }
        return tvSetInstance;
    }
}

---


## Question 3: Multiply all other elements except itself.

Input:

[2, 5, 3, 1]

Output:

[15, 6, 10, 30]

Solution: 

---

import java.util.*;

public class Main {
    public static void main(String[] args) {

        int[] arr = {2, 5, 3, 1};
        int[] result = new int[arr.length];

        int totalProduct = 1;

        // Find total product
        for (int num : arr) {
            totalProduct *= num;
        }

        // Divide total product by current element
        for (int i = 0; i < arr.length; i++) {
            result[i] = totalProduct / arr[i];
        }

        System.out.println(Arrays.toString(result));
    }
}

---

## Question 4: Find the character that repeats maximum times consecutively

Example:

aaabbccccdd

Output:

cccc

Solution:

---
public class Main {

    public static void main(String[] args) {

        String str = "aaabbccccdd";

        String maxSubstring = "";
        String currentSubstring = "";

        int maxCount = 0;
        int currentCount = 1;

        for (int i = 0; i < str.length(); i++) {

            if (i > 0 && str.charAt(i) == str.charAt(i - 1)) {
                currentCount++;
            } else {
                currentCount = 1;
            }

            if (currentCount > maxCount) {
                maxCount = currentCount;

                currentSubstring = String.valueOf(str.charAt(i))
                        .repeat(currentCount);

                maxSubstring = currentSubstring;
            }
        }

        System.out.println("Maximum repeated substring: " + maxSubstring);
    }
}
---

## Question 5: Write a code to find the number of time a number is repeated from list using hashmap

Input: {1, 2, 3, 2, 1, 4, 2, 3, 1}
Output: {1=3, 2=3, 3=2, 4=1}

Solution:
---

import java.util.*;

public class Main {

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(1, 2, 3, 2, 1, 4, 2);

        Map<Integer, Integer> map = new HashMap<>();

        for (Integer num : list) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        System.out.println(map);
    }
}

---

Question 6: 