package useful.menu.view;

import arc.Events;
import arc.func.Cons;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.game.EventType.PlayerLeave;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import useful.menu.MenuFormatter;
import useful.menu.view.State.StateKey;

public class Menu {

    public final ObjectMap<Player, MenuView> views = new ObjectMap<>();
    public final Seq<Cons<MenuView>> transformers = new Seq<>();

    public final int id;
    public Action closeAction = Action.none();

    {
        Events.on(PlayerLeave.class, event -> views.remove(event.player));

        this.id = Menus.registerMenu((player, option) -> {
            var view = views.remove(player);
            if (view == null) return;

            view.runOption(option);
        });
    }

    public MenuView show(Player player) {
        return show(player, new State());
    }

    public MenuView show(Player player, State state) {
        return show(player, state, view -> {});
    }

    public MenuView show(Player player, Cons<MenuView> transformer) {
        return show(player, new State(), transformer);
    }

    public MenuView show(Player player, State state, Cons<MenuView> transformer) {
        return views.get(player, () -> {
            var view = new MenuView(player, state, transformer);
            transformers.each(view::transform);
            view.transform(view.transformer);

            return view.show();
        });
    }

    public <T> MenuView showWith(Player player, StateKey<T> key, T value) {
        return show(player, State.with(key, value));
    }

    public <T> MenuView showWith(Player player, StateKey<T> key, T value, Cons<MenuView> transformer) {
        return show(player, State.with(key, value), transformer);
    }

    public <T1, T2> MenuView showWith(Player player, StateKey<T1> key1, T1 value1, StateKey<T2> key2, T2 value2) {
        return show(player, State.with(key1, value1).put(key2, value2));
    }

    public <T1, T2> MenuView showWith(Player player, StateKey<T1> key1, T1 value1, StateKey<T2> key2, T2 value2, Cons<MenuView> transformer) {
        return show(player, State.with(key1, value1).put(key2, value2), transformer);
    }

    public Menu transform(Cons<MenuView> transformer) {
        this.transformers.add(transformer);
        return this;
    }

    public Menu closed(Action closeAction) {
        this.closeAction = closeAction;
        return this;
    }

    public class MenuView {
        public final Player player;
        public final State state;

        public String title = "";
        public String content = "";

        public Seq<Seq<MenuOption>> options = new Seq<>();
        public Cons<MenuView> transformer;

        public MenuView(Player player, State state) {
            this(player, state, view -> {});
        }

        public MenuView(Player player, State state, Cons<MenuView> transformer) {
            this.player = player;
            this.state = state;
            this.transformer = transformer;
        }

        public void transform(Cons<MenuView> transformer) {
            transformer.get(this);
        }

        public MenuView show() {
            Call.menu(player.con, id, title, content, options.map(options -> options.map(MenuOption::button).toArray(String.class)).toArray(String[].class));
            return this;
        }

        public MenuView title(String title, Object... values) {
            this.title = MenuFormatter.format(title, player, values);
            return this;
        }

        public MenuView content(String content, Object... values) {
            this.content = MenuFormatter.format(content, player, values);
            return this;
        }

        public MenuView options(MenuOption[][] options) {
            this.options = Seq.with(options).map(Seq::with);
            return this;
        }

        public MenuOption getOption(int x, int y) {
            return options.get(y).get(x);
        }

        public MenuView setOption(int x, int y, MenuOption option) {
            this.options.get(y).set(x, option);
            return this;
        }

        public Seq<MenuOption> getOptionRow(int y) {
            return options.get(y);
        }

        public MenuView setOptionRow(int y, MenuOption... options) {
            this.options.set(y, Seq.with(options));
            return this;
        }

        public MenuView row() {
            this.options.add(new Seq<MenuOption>());
            return this;
        }

        public MenuView addOptionNone(String button, Object... values) {
            return addOption(MenuOption.none(MenuFormatter.format(button, player, values)));
        }

        public MenuView addOptionNone(char icon) {
            return addOption(MenuOption.none(icon));
        }

        public MenuView addOptionPlayer(String button, Cons<Player> cons, Object... values) {
            return addOption(MenuOption.player(MenuFormatter.format(button, player, values), cons));
        }

        public MenuView addOptionPlayer(char icon, Cons<Player> cons) {
            return addOption(MenuOption.player(icon, cons));
        }

        public MenuView addOption(String button, Action action, Object... values) {
            return addOption(MenuOption.of(MenuFormatter.format(button, player, values), action));
        }

        public MenuView addOption(char icon, Action action) {
            return addOption(MenuOption.of(icon, action));
        }

        public MenuView addOption(OptionData data) {
            return addOption(data.option(this));
        }

        public MenuView addOption(MenuOption option) {
            if (this.options.size == 0)
                this.row();

            this.options.peek().add(option);
            return this;
        }

        public MenuView addOptionRow(String button, Action action, Object... values) {
            return addOption(button, action, values).row();
        }

        public MenuView addOptionRow(char icon, Action action) {
            return addOption(icon, action).row();
        }

        public MenuView addOptionRow(OptionData data) {
            return addOption(data.option(this)).row();
        }

        public MenuView addOptionRow(MenuOption option) {
            return addOption(option).row();
        }

        public MenuView addOptionsRow(OptionData... datas) {
            return addOptionsRow((MenuOption[]) Seq.with(datas).map(data -> data.option(this)).toArray(MenuOption.class));
        }

        public MenuView addOptionsRow(MenuOption... options) {
            if (this.options.size > 0 && this.options.peek().size > 0)
                this.row();

            this.options.add(Seq.with(options));
            return this;
        }

        public MenuView addOptionsRow(int maxPerRow, OptionData... datas) {
            return addOptionsRow(maxPerRow, (MenuOption[]) Seq.with(datas).map(data -> data.option(this)).toArray(MenuOption.class));
        }

        public MenuView addOptionsRow(int maxPerRow, MenuOption... options) {
            if (this.options.size > 0 && this.options.peek().size > 0)
                this.row();

            for (var option : options) {
                if (this.options.peek().size >= maxPerRow)
                    this.row();

                addOption(option);
            }

            return this;
        }

        public MenuOption getOption(int id) {
            if (id < 0) return null;

            int i = 0;
            for (var row : options) {
                i += row.size;
                if (i > id)
                    return row.get(id - i + row.size);
            }

            return null;
        }

        public void runOption(int id) {
            var option = getOption(id);
            if (option == null) {
                closeAction.get(this);
                return;
            }

            option.action().get(this);
        }

        public Menu getMenu() {
            return Menu.this;
        }
    }

    public interface OptionData {
        MenuOption option(MenuView view);
    }
}