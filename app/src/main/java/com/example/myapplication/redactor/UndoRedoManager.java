package com.example.myapplication.redactor;

import java.util.List;
import java.util.Stack;

public class UndoRedoManager {
    private Stack<Action> undoStack = new Stack<>();
    private Stack<Action> redoStack = new Stack<>();
    private List<DesignElement> elements;

    public UndoRedoManager(List<DesignElement> elements) {
        this.elements = elements;
    }

    public void execute(Action action) {
        action.execute();
        undoStack.push(action);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Action action = undoStack.pop();
            action.undo();
            redoStack.push(action);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Action action = redoStack.pop();
            action.execute();
            undoStack.push(action);
        }
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    // Абстрактное действие
    public interface Action {
        void execute();
        void undo();
    }

    // Конкретные действия
    public static class AddElementAction implements Action {
        private List<DesignElement> elements;
        private DesignElement element;

        public AddElementAction(List<DesignElement> elements, DesignElement element) {
            this.elements = elements;
            this.element = element;
        }

        @Override
        public void execute() {
            elements.add(element);
        }

        @Override
        public void undo() {
            elements.remove(element);
        }
    }

    public static class RemoveElementAction implements Action {
        private List<DesignElement> elements;
        private DesignElement element;

        public RemoveElementAction(List<DesignElement> elements, DesignElement element) {
            this.elements = elements;
            this.element = element;
        }

        @Override
        public void execute() {
            elements.remove(element);
        }

        @Override
        public void undo() {
            elements.add(element);
        }
    }

    public static class MoveElementAction implements Action {
        private DesignElement element;
        private float oldX, oldY;
        private float newX, newY;

        public MoveElementAction(DesignElement element, float oldX, float oldY, float newX, float newY) {
            this.element = element;
            this.oldX = oldX;
            this.oldY = oldY;
            this.newX = newX;
            this.newY = newY;
        }

        @Override
        public void execute() {
            element.setX(newX);
            element.setY(newY);
        }

        @Override
        public void undo() {
            element.setX(oldX);
            element.setY(oldY);
        }
    }
}