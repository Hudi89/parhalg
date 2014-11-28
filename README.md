Párhuzamos Algoritmusok Gy. - Projektmunka
=======
A program célja, hogy egy windowsos kliens által kijelölt mappákat a rendszer automatikusan mentse folyamatosan egy távoli hálózaton található Raspi-hoz csatolt pendrive-ra. 
Ha a tárterület fogy akkor jeleznie kell a kliensnek.
A raspi egyszerre több kliensel is kell tudjon foglalkozni.

Kliens program
--------
- A felhasználó kijelöl mappákat ami megjelenik egy táblázatban (mappa elérési útja, utolsó mentés, mappa méret, állapot (itt jelenik meg, ha mentés alatt áll)) 
- Háttérben egy thread figyeli folyamatosan a változásokat, ha változás történt akkor meghívja a kliens oldali core-t, hogy csináljon mentést az adott fájlra, miközben a fájlok nem módosíthatóak más által. Mappákban új létrejött fájloknak a figyelésére figyelni kell.

Szerver oldal (Raspi)
--------
- Fogadja folyamatosan a kliensek csatlakozását
- Ha kliens küld archiválásra egy fájlt akkor azt lementi a pendrive-ra.
- A tárterületet folyamatosan figyelni kell, ha 1GB alá megy a terület akkor minden socketre küld egy akciót.

Kommunikációs parancsok
--------
HELLO - üdövzlő üzenet, amit a kommunikcáió indítására szolgál

USER_ERROR(string errorMsg) - üzenet, amit a kliens a felhasználó számára kiad
- < 1GB
- Nem találja a pendrive-t
- Elfogyott a tárterület

CLOSE - lezáró parancs a kliensnek, hogy valamilyen ok folytán nem tudja tovább ellátni a backup szerepét (ezt válószínűleg megelőzi egy hibaüzenet)
