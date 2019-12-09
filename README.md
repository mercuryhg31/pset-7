# Problem Set 7

It's time to put your skills to the test. Rather than solving a disjointed set of exercises, this problem set focuses on building a persistent application backed by a real database. Portions of the application are already implemented for you. It's your job to fill in the pieces.

[Skeleton Repository](https://github.com/ap-java-ucvts/pset-7-skeleton)

## Getting Started

To get started, create a [GitHub](https://github.com/) repository to store your code. When you're finished, clone my skeleton to get all of the starter code and instructions. Setup a remote to push your code to your repository instead of mine.

### Setup

1. Login to your GitHub account and create a new repository named `pset-7`.
2. In the terminal, navigate to your `APCSA` directory on the `Desktop`.
```
$ cd ~/Desktop/APCSA
```
3. Clone my skeleton repository into a directory named `pset-7`.
```
$ git clone git@github.com:ap-java-ucvts/pset-7-skeleton.git pset-7
```
4. Change into your newly created `pset-7` directory.
```
$ cd pset-7
```
5. Overwrite the remote, which originally points at my skeleton repository.
```
$ git remote rename origin upstream
```
6. Add a new remote that points at your `pset-7` repository. Replace `YOUR-USERNAME` with your actual username.
```
$ git remote add origin git@github.com:YOUR-USERNAME/pset-7.git
```
7. Open Eclipse. Click `File > Import...`.
8. Choose `General > Projects from Folder or Archive` and click `Next >`.
9. Click `Directory...` and navigate to the `pset-7` folder.
10. Click `Open`, and then `Finish`.
11. Right-click the `pset-7` project and click `New > Folder`.
12. Type `data` and click `Finish`.

Although there are unresolved compilation errors, you can now launch the application. It won't do a whole lot yet, but it will build the database.

## Overview

You'll be creating a grading system much like the PowerSchool application we all know and love. You'll need to implement the application for four separate access levels: root, school administrator, teacher, and student. The features and functionalities available in the application will depend on the type of account with which a user logs in.

### Use Cases

Many of these use cases will familiar to you based on your own experiences with PowerSchool. Naturally, those specific to school administators and teachers will be quite different. We'll separate use cases based on students, teachers, administrators, and the all-powerful root user.

#### Student

* Login to an account
* View course grades
* View assignment grades by course
* Change password
* Logout of an account

#### Teacher

* Login to an account
* View enrollment by course
* Add assignment
* Delete assignment
* Enter grade for assignment
* Change password
* Logout of an account

#### Administrator

* Login to an account
* View faculty
* View faculty by department
* View enrollment
* View enrollment by grade
* View enrollment by course
* Change password
* Logout of an account

#### Root

* Login to an account
* Reset password for another account
* Factory reset database
* Logout of an account
* Shutdown application

As you begin to test your code, which you should do early, thoroughly, and often, these use cases should give you a very good starting point. There's a lot of functionality to implement here, so I'd recommend focusing on one user type (and one use case for that user type!) at a time.

## Requirements

In developing this application, you'll need to meet specific requirements for each user. The features and options available to you depend entirely on the type of user that is logged in. It's your job to make this distinction.

### User Interface

Coming soon!

### Data Persistence

All changes must persist between separate logins (across all user account types) and application shutdowns and startups. The only instance in which data persistence is acceptably lost is in the case of a root-authorized factory reset.

Rather than a flat file, we're persisting all changes to an embedded SQLite database. Many (but not all!) of the database methods have been written for you. You'll need to finish up the data access layer of the application, as well as integrate the provided methods what your own code.

To make your lives a little easier, I recommend downloading a browser application for the SQLite database. [DB Browser for SQLite](https://sqlitebrowser.org/) is a pretty good option. Plus, it's free. There are others out there (some free, some not) if you aren't a fan of that one.

### Fields

Let's walk through each of the field requirements as defined in the various classes, but most importantly as stored in the embedded database tables.

#### Users

The table structure, including any table- column-level constraints, are clearly defined in the `setup.sql` file. Time to brush up on those SQL skills! Aside from that, though, there are few formatting issues you'll need to understand.

* The `userId` field is an autoincremented integer, starting from 1. Nothing to see here.
* The `account_type` field is either `root`, `administrator`, `teacher`, or `student`. This is how you'll determine which interface to display and which features to offer.
* The `username` field is always formatted as *firstinitiallastname*, like so: `rwilson`. This field needs to be unique, so you can add numbers (starting with 1 and incrementing by 1 from there) to remedy this as needed (i.e., `rwilson1`).
* The `auth` field is a 32-character MD5 hash representing the user's password. The default password for every user is their username. This needs to be changed upon their first login. Always use the `Utils.getHash` function to hash plaintext passwords.
* The `last_login` field is a timestamp (formatted as `YYYY-MM-DD HH:MM:SS.FFF`) representing the last time the user logged into the system. By default, this is `0000-00-00 00:00:00.000`, which is how you'll know if you need to prompt first-time users to change their default passwords).

#### Departments

The table structure, including any table- column-level constraints, are clearly defined in the `setup.sql` file. Time to brush up on those SQL skills! There's not a whole lot going on here of interest.

* The `department_id` field is an autoincremented integer, starting from 1.
* The `title` field is the name of the department, which can be `Computer Science`, `English`, `History`, `Mathematics`, `Phyiscal Education`, or `Science`.

#### Administrators

The table structure, including any table- column-level constraints, are clearly defined in the `setup.sql` file. Time to brush up on those SQL skills! Aside from that, though, there are few formatting issues you'll need to understand.

* The `teacher_id` field is an autoincremented integer, starting from 1.
* The `first_name` field is the administrator's first name.
* The `last_name` field is the administrator's last name.
* The `job_title` field is the position the administrator holds within the school. This can be either `Principal` or `Supervisor`.
* The `user_id` field is a reference (called a *foreign key*) to the `user` table. It's a way of associating each administrator with exactly one user account.

As you might've noticed, the `Administrator` class extends the `User` class. This means, in practice, the `activeUser` (when that user is an admnistrator) will store the attibutes of both an `Administrator` and a `User`.

#### Teachers

The table structure, including any table- column-level constraints, are clearly defined in the `setup.sql` file. Time to brush up on those SQL skills! Aside from that, though, there are few formatting issues you'll need to understand.

* The `administrator_id` field is an autoincremented integer, starting from 1.
* The `first_name` field is the administrator's first name.
* The `last_name` field is the administrator's last name.
* The `department_id` field is a reference (called a *foreign key*) to the `departments` table. It's a way of associating each teacher with exactly one department.
* The `user_id` field is a reference (called a *foreign key*) to the `user` table. It's a way of associating each teacher with exactly one user account.

As you might've noticed, the `Teacher` class extends the `User` class. This means, in practice, the `activeUser` (when that user is a teacher) will store the attibutes of both an `Teacher` and a `User`.

#### Students

The table structure, including any table- column-level constraints, are clearly defined in the `setup.sql` file. Time to brush up on those SQL skills! Aside from that, though, there are few formatting issues you'll need to understand.

* The `student_id` field is an autoincremented integer, starting from 1.
* The `first_name` field is the administrator's first name.
* The `last_name` field is the administrator's last name.
* The `graduation` field is the year in which the student is set to graduate. This is always a four-digit year.
* The `grade_level` field is the numeric representation of freshman, sophomore, junior, or senior. This translates into either `9`, `10`, `11`, or `12`.
* The `gpa` is the computed grade point average, always rounded to four digits of precision. This is calcuated with each grade entry; prior to any grade entries, it defaults to `0.0`.
* The `class_rank` is the numeric ranking based on `gpa`. This, too, is calculated iwth each grade entry; prior to any grade entries, it defaults to `0`.
* The `user_id` field is a reference (called a *foreign key*) to the `user` table. It's a way of associating each student with exactly one user account.

As you might've noticed, the `Student` class extends the `User` class. This means, in practice, the `activeUser` (when that user is a student) will store the attibutes of both an `Student` and a `User`.

#### Courses

The table structure, including any table- column-level constraints, are clearly defined in the `setup.sql` file. Time to brush up on those SQL skills! Aside from that, though, there are few formatting issues you'll need to understand.

* The `course_id` field is an autoincremented integer, starting from 1. Every time you add a course, this number increases.
* The `department_id` field is a reference (called a *foreign key*) to the `departments` table. It's a way of associating each course with exactly one department.
* The `course_no` field is a unique, text-based course identification system. Each course number starts with a two-character identifier (`CS` for `Computer Science`, `EN` for `English`, `HI` for `History`, `MA` for `Mathematics`, `PE` for `Physical Education`, and `SC` for `Science`) followed by a four-digit number. Higher numbers indicate more advanced courses.
* The `title` field is the name of the course.
* The `credit_hours` field is the number of credits the course is worth.
* The `weight` field indicates how the course is weighted in terms of GPA calculations. Standard courses have a `1.0` weighting, while AP courses have a `1.5` weighting.
* The `enrollment` field tracks how many students are currently enrolled in the course.
* The `capacity` field is the the maximum number of students who may enroll in a course at any given time.

#### Course Grades

The table structure, including any table- column-level constraints, are clearly defined in the `setup.sql` file. Time to brush up on those SQL skills! Aside from that, though, there are few formatting issues you'll need to understand.

* The `course_id` field is a reference (called a *foreign key*) to the `courses` table. It's a way of associating each grade with exactly one course.
* The `student_id` field is a reference (called a *foreign key*) to the `students` table. It's a way of associating each grade with exactly one student.
The `mp1` field is the first marking period grade.
The `mp2` field is the second marking period grade.
The `midterm_exam` field is the midterm exam grade.
The `mp3` field is the third marking period grade.
The `mp4` field is the fourth marking period grade.
The `final_exam` field is the final exam grade.
The `grade` field is the calculated course grade based on the marking period and exam grades (20% per marking period, 10% per exam).

Until now, all of our tables have used a single primary key column. The `course_grades` table uses the `course_id` and `student_id` columns as a composite primary key. This means that every row in the table must have a unique combination of these two column values.

#### Assignments

The table structure, including any table- column-level constraints, are clearly defined in the `setup.sql` file. Time to brush up on those SQL skills! Aside from that, though, there are few formatting issues you'll need to understand.

* The `course_id` field is a reference (called a *foreign key*) to the `courses` table. It's a way of associating each assignment with exactly one course.
* The `assignment_id` field is an autogenerated integer by course.
* The `title` field is the name of the assignment.
* The `point_value` field is the number of points the assignment is worth. This must be between 1 and 100.

The `assignments` table uses the `course_id`, `assignment_id`, and `title` columns as a composite primary key. This means that every row in the table must have a unique combination of these three column values.

#### Assignment Grades

The table structure, including any table- column-level constraints, are clearly defined in the `setup.sql` file. Time to brush up on those SQL skills! Aside from that, though, there are few formatting issues you'll need to understand.

* The `course_id` field is a reference (called a *foreign key*) to the `courses` table. It's a way of associating each assignment grade with exactly one course.
* The `assignment_id` field is a reference (called a *foreign key*) to the `assignments` table. It's a way of associating each assignment grade with exactly one assignment.
* The `student_id` field is a reference (called a *foreign key*) to the `students` table. It's a way of associating each assignment grade with exactly one student.
* The `points_earned` field is the number of points the student earned on that assignment. This value must be between 0 and `points_possible`.
* The `points_possible` field is the total number of points a student could have possibly earned on this assignment. This must be between 1 and 100.

The `assignment_grades` table uses the `course_id`, `assignment_id`, and `assignment_id` columns as a composite primary key. This means that every row in the table must have a unique combination of these three column values.

### Teams

If you wish, you can work with one other person on this assignment. It is your responsibility to agree on a design, your development plan, the division of labor, and (perhaps, most importantly!) timelines. You'll submit your problem set as a team, and will receive a grade as a team.

If you elect to work in a pair, you are to create a single repository configured so that each of you has permission push and pull changes to and from it. Let me know if you need help setting this up, but it should be fairly straightforward to add a collaborator.

## Deadline

Please read very carefully. Historically, most students lose points on problem sets for simply failing to read the instructions and requirements.

* December 19, 2019, at 11:59pm.

If you submit your problem set at midnight (i.e., December 20, 2019, at 12:00am), it is considered **late**!

### Submission Requirements

* Your code **must** compile. Code that fails to meet this minimum requirement will not be accepted.
* There must be **at least** 50 unique commits to your repository.
* Your code must meet each requirement outlined in the *Overview* and *Requirements* sections.
* Your code must adhere to the course style guidelines.

Happy coding!


