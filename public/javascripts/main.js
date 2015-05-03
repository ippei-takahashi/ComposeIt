/**
 * Created by pony on 15/04/22.
 */

//this application only has one module: todo
var todo = {};

//for simplicity, we use this module to namespace the model classes

//the Todo class has two properties
todo.Todo = function(data) {
    this.id = m.prop();
    this.description = m.prop(data.description);
    this.done = m.prop(false);
};

//the TodoList class is a list of Todo's
todo.TodoList = function() {
    return m.request({method: "GET", url: "/todo"}).then(function(list) {
        return list;
    });
};
todo.TodoList.save = function(todoList) {
    return function() {
        todoList.forEach(function (todo) {
            m.request({
                method: "POST",
                url: "/todo",
                data: todo
            });
        });
    };
};

//the view-model tracks a running list of todos,
//stores a description for new todos before they are created
//and takes care of the logic surrounding when adding is permitted
//and clearing the input after adding a todo to the list
todo.vm = (function() {
    var vm = {}
    vm.init = function() {
        //a running list of todos
        vm.list = todo.TodoList();

        //a slot to store the name of a new todo before it is created
        vm.description = m.prop("");

        //adds a todo to the list, and clears the description field for user convenience
        vm.add = function() {
            if (vm.description()) {
                vm.list.push(new todo.Todo({description: vm.description()}));
                vm.description("");
            }
        };

        vm.save = todo.TodoList.save(vm.list);
    }
    return vm
}())

//the controller defines what part of the model is relevant for the current page
//in our case, there's only one view-model that handles everything
todo.controller = function() {
    todo.vm.init()
}

//here's the view
todo.view = function() {
    return m("html", [
        m("body", [
            m("input", {onchange: m.withAttr("value", todo.vm.description), value: todo.vm.description()}),
            m("button", {onclick: todo.vm.add}, "Add"),
            m("table", [
                todo.vm.list.map(function(task, index) {
                    return m("tr", [
                        m("td", [
                            m("input[type=checkbox]", {onclick: m.withAttr("checked", task.done), checked: task.done()})
                        ]),
                        m("td", {style: {textDecoration: task.done() ? "line-through" : "none"}}, task.description()),
                    ])
                })
            ]),
            m("button", {onclick: todo.vm.save}, "Save")
        ])
    ]);
};

//initialize the application
m.module(document, {controller: todo.controller, view: todo.view});