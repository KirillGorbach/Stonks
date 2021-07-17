# Stonks

Stonks - приложение для просмотра популярных акций.


<div display="inline-block">
<img src="https://thumb.cloud.mail.ru/weblink/thumb/xw1/k2MG/BJPahPkkZ/Screenshot_2021-04-04-22-09-44-181_com.example.stonks.jpg" margin="2px" width="200">
<img src="https://thumb.cloud.mail.ru/weblink/thumb/xw1/k2MG/BJPahPkkZ/Screenshot_2021-04-04-22-09-49-890_com.example.stonks.jpg" margin="2px" width="200">
<img src="https://thumb.cloud.mail.ru/weblink/thumb/xw1/k2MG/BJPahPkkZ/Screenshot_2021-04-04-22-10-04-896_com.example.stonks.jpg" margin="2px" width="200">
</div>



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

* [com.github.PhilJay:MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) - графики


[Скриншоты и apk](https://cloud.mail.ru/public/k2MG/BJPahPkkZ)

ВНИМАНИЕ!!!

Из-за ограничений бесплатной подписки mboum просмотр новостей и отображение графика доступны только для акций AAPL (Apple Inc.) и F (Ford motor company)

Из-за ограничений ограничений finnhub.io (количество запросов в минуту) некоторые данные (цены, графики) могут загрузиться в течение минуты. Также из соображений экономии запросов количество отображаемых акций ограничено пятнадцатью



