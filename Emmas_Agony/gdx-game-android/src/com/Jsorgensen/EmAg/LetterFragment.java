package com.Jsorgensen.EmAg;
import android.app.*;
import android.view.*;
import android.os.*;
import android.widget.*;

public class LetterFragment extends Fragment
{

	static int letter_page = 0;
	
	public void LetterFragment(){
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		
        return inflater.inflate(R.layout.letter1, container, false);
	}
	
	public void letterClickMethod(View v){
		letter_page++;
		
	}
}
