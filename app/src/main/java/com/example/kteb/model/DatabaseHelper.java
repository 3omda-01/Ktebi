package com.example.kteb.model;

import androidx.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DatabaseHelper {
    private static DatabaseHelper instance;
    private FirebaseFirestore db;
    private String userId;

    public interface OnBooksLoadedListener {
        void onBooksLoaded(List<Book> books);
    }

    public interface OnSessionsLoadedListener {
        void onSessionsLoaded(List<ReadingSession> sessions);
    }

    public interface OnOperationCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    private DatabaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserIdFromAuth() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            this.userId = user.getUid();
        } else {
            this.userId = "demo_user";
        }
    }

    private String getUserId() {
        if (userId == null) {
            setUserIdFromAuth();
        }
        return userId;
    }

    public void addBook(Book book, OnOperationCompleteListener listener) {
        if (book == null) {
            if (listener != null) {
                listener.onFailure(new IllegalArgumentException("Book cannot be null"));
            }
            return;
        }
        
        CollectionReference booksRef = db.collection("users")
                .document(getUserId())
                .collection("books");
        
        booksRef.add(book)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            if (listener != null) {
                                listener.onSuccess();
                            }
                        } else {
                            Exception e = task.getException();
                            e.printStackTrace();
                            if (listener != null) {
                                listener.onFailure(e);
                            }
                        }
                    }
                });
    }

    public void getBooks(OnBooksLoadedListener listener) {
        CollectionReference booksRef = db.collection("users")
                .document(getUserId())
                .collection("books");
        
        booksRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && listener != null) {
                            List<Book> books = new CopyOnWriteArrayList<>();
                            for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult()) {
                                Book book = doc.toObject(Book.class);
                                book.setId(doc.getId());
                                books.add(book);
                            }
                            listener.onBooksLoaded(books);
                        }
                    }
                });
    }

    public void getBooksByCategory(String category, OnBooksLoadedListener listener) {
        CollectionReference booksRef = db.collection("users")
                .document(getUserId())
                .collection("books");
        
        Query query = booksRef.whereEqualTo("category", category);
        
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && listener != null) {
                            List<Book> books = new CopyOnWriteArrayList<>();
                            for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult()) {
                                Book book = doc.toObject(Book.class);
                                book.setId(doc.getId());
                                books.add(book);
                            }
                            listener.onBooksLoaded(books);
                        }
                    }
                });
    }

    public void updateBook(Book book, OnOperationCompleteListener listener) {
        DocumentReference bookRef = db.collection("users")
                .document(getUserId())
                .collection("books")
                .document(book.getId());
        
        bookRef.set(book)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful() && listener != null) {
                            listener.onSuccess();
                        } else if (listener != null) {
                            listener.onFailure(task.getException());
                        }
                    }
                });
    }

    public void deleteBook(String bookId, OnOperationCompleteListener listener) {
        DocumentReference bookRef = db.collection("users")
                .document(getUserId())
                .collection("books")
                .document(bookId);
        
        bookRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful() && listener != null) {
                            listener.onSuccess();
                        } else if (listener != null) {
                            listener.onFailure(task.getException());
                        }
                    }
                });
    }

    public void addReadingSession(ReadingSession session, OnOperationCompleteListener listener) {
        CollectionReference sessionsRef = db.collection("users")
                .document(getUserId())
                .collection("readingSessions");
        
        sessionsRef.add(session)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful() && listener != null) {
                            listener.onSuccess();
                        } else if (listener != null) {
                            listener.onFailure(task.getException());
                        }
                    }
                });
    }

    public void getReadingSessions(OnSessionsLoadedListener listener) {
        CollectionReference sessionsRef = db.collection("users")
                .document(getUserId())
                .collection("readingSessions");
        
        sessionsRef.orderBy("startTime", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && listener != null) {
                            List<ReadingSession> sessions = new CopyOnWriteArrayList<>();
                            for (com.google.firebase.firestore.DocumentSnapshot doc : task.getResult()) {
                                ReadingSession session = doc.toObject(ReadingSession.class);
                                session.setId(doc.getId());
                                sessions.add(session);
                            }
                            listener.onSessionsLoaded(sessions);
                        }
                    }
                });
    }
}