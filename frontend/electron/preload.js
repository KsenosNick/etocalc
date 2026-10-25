import { contextBridge } from 'electron';
// Мост для безопасного общения между main и renderer
contextBridge.exposeInMainWorld('electronAPI', {
// Пока пусто, позже добавим API для работы с файлами, диалогами и т.п.
});
