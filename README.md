# 📚 **Language Learning Game**

*A Java Application with Console & GUI (Swing + AWT) Implementations*

---

## **Overview**

The **Language Learning Game** is a Java-based interactive learning application designed to improve vocabulary, sentence formation, and comprehension skills through quizzes and gamified learning techniques.

This project now provides:

* ✅ **Console-Based Gameplay (CLI)**
* ✅ **GUI-Based Gameplay (Swing + AWT)**
* ✅ **Modular OOP Architecture**
* ✅ **Score, Achievements & Mistake Tracking**
* ✅ **Different Question Types (Word, Sentence, Match Pair)**
* ✅ **Extensible systems for new question formats**

It is designed for academic evaluation as well as showcasing **strong Java fundamentals**, **object-oriented design**, and **GUI development**.

---

## **Key Features**

### **1. User Management**

* Player profile creation
* Tracks user score, achievements, mistakes, and progress
* Persistent in-memory tracking throughout the session

---

### **2. Multiple Question Types**

* Supports a diverse quiz system:
* Word Meaning Questions
* Sentence Formation Questions
* Match-the-Pair Questions
* Multiple-choice or custom questions (extendable)
* All question types inherit from a common base structure using OOP principles.

### **3. GUI Version (Swing + AWT)**

The game includes a full desktop GUI built using:

* JFrame
* JPanel
* JLabel
* JButton
* JTextField
* JTextArea
* JTable
* JOptionPane

GUI Features include:

* 🎨 Welcome Screen
* 📝 Select Question Type
* ❓ Interactive Questions with Input Components
* ⭐ Score and Achievement Popups
* ❗ Mistake Alerts
* 📊 Progress Summary Window

Designed with clean layouts, aesthetic components, and user-friendly navigation.

### ***4. Console Version (CLI)***

* Menu-driven
* Scanner-driven input
* Question-by-question interaction
* Clean textual feedback
Detailed progress summary

### ***5. Score & Achievement System***

* Tracks points for correct answers
* Awards badges based on milestones
* Highlights accuracy percentage
* Encourages continued improvement

### ***6. Mistake Manager***

* Logs mistakes
* Displays incorrect answers
* Provides repeat-practice suggestions
* Integrated into both GUI & Console versions

## **Tech Stack / Concepts Used**

| **Category**              | **Concepts**                                                                 |
| ------------------------- | ----------------------------------------------------------------------------- |
| **OOP**                   | Abstraction, Encapsulation, Inheritance, Polymorphism                         |
| **Advanced OOP**          | Modular class design, extensible question/resource system                     |
| **Exception Handling**    | try-catch blocks, input validation, safe error handling                       |
| **GUI Development**       | Swing: JFrame, JPanel, JButton, JTextField, JTable, JScrollPane               |
| **AWT**                   | Event handling, Layout Managers (FlowLayout, BorderLayout, GridLayout)        |
| **Collections**           | ArrayList, HashMap (if used), List-based data structures                      |
| **Design Principles**     | Single Responsibility Principle, clean architecture, layered class structure   |
| **Console I/O**           | Scanner-based input, menu-driven navigation                                   |

---
## **Class Architecture**

### **Core Classes**

- `User` – Stores player information, progress data, and performance history  
- `Quiz` – Controls overall quiz flow (loading questions, navigating levels, scoring, completion logic)  

### **Question Hierarchy**

- `Question` (Base Class) – Abstract/general structure for all questions  
- `WordQuestion` – Handles vocabulary/word-based questions  
- `SentenceQuestion` – Handles sentence formation or grammar questions  
- `MatchPair` – Manages match-the-following style questions  

---

### **Helper / Manager Classes**

- `ScoreManager` – Calculates, updates, and stores quiz scores  
- `ProgressTracker` – Tracks levels completed, attempts, streaks, and overall user progress  
- `AchievementSystem` – Unlocks badges, achievements, milestones based on performance  
- `MistakeManager` – Stores incorrect answers and generates review sessions  
- `GUIManager` – Manages Swing UI screens, transitions, button actions, and components  


Each module is designed to be reusable and extendable.

## **Learning Outcomes**

By building this project, we demonstrated proficiency in:

* Object-oriented software design
* Java Swing GUI development
* Event-driven programming
* Multi-module project structuring
* Data validation & user interaction design
* Implementing game mechanics using OOP
* Clean interface and class separation

Perfect for: **Academic submission,** **Java course evaluation**,**Resume & GitHub portfolio**,**Viva and technical interviews**

## **Credits**

Developed as part of the Object Oriented Programming Curriculum (2025–26).

## Author
**Bhavya kodipyaka** - [kbhavya2506@gmail.com]
  Github:[https://github.com/kbhavya2506-cell]


**Bhavan Ram**
