package fr.agendapp.app.pages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import fr.agendapp.app.App;
import fr.agendapp.app.R;
import fr.agendapp.app.factories.NotificationFactory;
import fr.agendapp.app.objects.Comment;
import fr.agendapp.app.objects.Work;

public class CommentPage extends AppCompatActivity {

    private static Work w;
    private EditText text;

    private CommentAdapter adapter;

    public static void setWork(Work work) {
        w = work;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On va manipuler l'objet static Work w qui devrait être défini avant d'arriver sur cette page
        try {
            // Si l'objet Work w est bien disponible, on peut initialiser (inflate) la vue
            setContentView(R.layout.activity_comment);
            // Liste de commentaires associés à ce devoir
            // Section contenant les invitations à des groupes
            RecyclerView commentList = (RecyclerView) findViewById(R.id.commentlist);
            adapter = new CommentAdapter();
            commentList.setHasFixedSize(false);
            commentList.setLayoutManager(new LinearLayoutManager(this));
            commentList.setAdapter(adapter);
            // Texte du nouveau commentaire
            text = (EditText) findViewById(R.id.commentnew);
            // Bouton d'envoi
            findViewById(R.id.commentadd).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendComment();
                        }
                    }
            );
        } catch (NullPointerException e) {
            // Si ce n'est pas le cas, on mentionne l'erreur au logcat
            Log.e(App.TAG, "Page commentaire ouverte sans avoir défini de devoir au prealable !");
            // On ajoute une notification à l'utilisateur TODO resources
            NotificationFactory.add(this, 1, "Erreur", "Action impossible");
            // Puis on le redirige vers la page principale
            startActivity(new Intent(this, MainPage.class));
        }
    }

    private void sendComment() {
        String texte = text.getText().toString();
        Comment c = new Comment();// TODO
    }


    public static class CommentAdapter extends RecyclerView.Adapter<Comment.CommentHolder> {

        @Override
        public Comment.CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Comment.CommentHolder(LayoutInflater.from(parent.getContext()), parent, this);
        }

        @Override
        public void onBindViewHolder(Comment.CommentHolder holder, int position) {
            holder.setComment(w.getComments().get(position));
        }

        @Override
        public int getItemCount() {
            return w.getComments().size();
        }
    }

}
