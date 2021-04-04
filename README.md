# Stonks

Stonks - приложение для просмотра популярных акций.

### Функционал:

* Загрузка акций с сайта mboum.com

* Загрузка цен на акции с сайта finnhub.io

* Просмотр новостей, связанных с акцией от mboum.com

* Переход по ссылке на источник новостей (нажать на новость)

* Поиск акций по названию и тикеру

* Добавление акций в избранное

* Загрузка логотипов компаний с сайта finnhub.io

* Отображение графика изменений стоимости акции за две недели

* Выбор темы (дневная/ночная)

### Особенности

* Все изображения кэшируются

* Функция SwipeRefresh - обновление данных акций при свайпе списка вверх

* Все данные получаются асинхронно, приложение ни в коем случае не блокируется

* При отсутствии сети или сбоях приложение будет повторять попытки подключения раз в секунду

* Сохраняется состояние фрагментов – корректное поведение при повороте экрана

Приложение раелизует паттерн MVVP средствами Android Architecture Components

### Использованы библиотеки:

* android.arch.lifecycle

* androidx.swiperefreshlayout

* androidx.room


ВНИМАНИЕ!!!
Из-за ограничений бесплатной подписки mboum просмотр новостей и отображениеграфика доступны только для акций AAPL (Apple Inc.) и F (Ford motor company)
Из-за ограничений ограничений finnhub.io (количество запросов в минуту) может некоторые данные (цены, графики) могут загрузиться в течение минуты. Также из соображений экономии запросов количество отображаемых акций ограничено пятнадцатью



