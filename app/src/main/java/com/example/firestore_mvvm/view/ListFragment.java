package com.example.firestore_mvvm.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.firestore_mvvm.R;
import com.example.firestore_mvvm.adapter.ContactAdapter;
import com.example.firestore_mvvm.dialogue.DetailsDialogue;
import com.example.firestore_mvvm.model.ContactUser;
import com.example.firestore_mvvm.viewmodel.ContactViewModel;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class ListFragment extends Fragment implements ContactAdapter.ClickInterface{
    private SearchView searchView;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private List<ContactUser> userList= new ArrayList<>();

    //view Model
    private ContactViewModel contactViewModel;


    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        setUpRecycle();
        //find Section...
        searchView= view.findViewById(R.id.searchViewId);
        recyclerView= view.findViewById(R.id.recycleViewId);
        adapter= new ContactAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                final AlertDialog dialogue= new SpotsDialog.Builder().setContext(getActivity()).setTheme(R.style.Custom).setCancelable(true).build();
                dialogue.show();
                contactViewModel.search(s);
                contactViewModel.searchLiveData.observe(getActivity(), new Observer<List<ContactUser>>() {
                    @Override
                    public void onChanged(List<ContactUser> contactUsers) {
                        dialogue.dismiss();
                        userList= contactUsers;
                        adapter.getContactList(userList);
                        recyclerView.setAdapter(adapter);
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }



    private void setUpRecycle() {
        final AlertDialog dialogue= new SpotsDialog.Builder().setContext(getActivity()).setTheme(R.style.Custom).setCancelable(true).build();
        dialogue.show();
        contactViewModel.show();
        contactViewModel.getContactLiveData.observe(getActivity(), new Observer<List<ContactUser>>() {
            @Override
            public void onChanged(List<ContactUser> contactUsers) {
                dialogue.dismiss();
                userList= contactUsers;
                //userList.clear();
                adapter.getContactList(userList);
                recyclerView.setAdapter(adapter);
                //Toast.makeText(getActivity(), ""+contactUsers.get(0).getContactName(), Toast.LENGTH_SHORT).show(); //check it before set recycle

            }
        });
    }

    private void initViewModel() {
        contactViewModel= new ViewModelProvider(getActivity(),ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()))
                .get(ContactViewModel.class);
    }


    @Override
    public void onItemClick(int position) {
        //Toast.makeText(getActivity(), ""+position, Toast.LENGTH_SHORT).show();
        openDetailsDialogue(position);
    }
    @Override
    public void onLongItemClick(final int position) {
        final String id= userList.get(position).contactId;
        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());
        alertDialogue.setTitle("Delete Contact").setMessage("Do you want to Delete ?").setIcon(R.drawable.ic_delete);
        alertDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                contactViewModel.delete(id);
                Toast.makeText(getActivity(), "Delete", Toast.LENGTH_SHORT).show();
                userList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        });
        alertDialogue.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog= alertDialogue.create();
        dialog.show();


        //Toast.makeText(getActivity(), position+": "+userList.get(position).contactId, Toast.LENGTH_SHORT).show();

    }

    private void openDetailsDialogue(int position) {
        DetailsDialogue dialogue= new DetailsDialogue(userList,position,ListFragment.this);
        dialogue.show(getChildFragmentManager(),"Details Dialogue");

    }
}