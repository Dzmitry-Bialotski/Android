package by.belotskiy.battleship.view_model.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import by.belotskiy.battleship.view_model.GameViewModel;

public class GameFactory extends ViewModelProvider.NewInstanceFactory{
    private String roomId;
    public GameFactory(String roomId){
        super();
        this.roomId = roomId;
    }
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == GameViewModel.class) {
            return (T) new GameViewModel(roomId);
        }
        return null;
    }
}
