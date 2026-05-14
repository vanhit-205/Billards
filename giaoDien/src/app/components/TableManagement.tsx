import { useState, useEffect } from "react";
import { Play, Pause, RotateCcw } from "lucide-react";

type Table = {
  id: number;
  number: number;
  playTime: number;
  isPlaying: boolean;
};

export function TableManagement() {
  const [tables, setTables] = useState<Table[]>([
    { id: 1, number: 1, playTime: 0, isPlaying: false },
    { id: 2, number: 2, playTime: 0, isPlaying: false },
    { id: 3, number: 3, playTime: 0, isPlaying: false },
    { id: 4, number: 4, playTime: 0, isPlaying: false },
    { id: 5, number: 5, playTime: 0, isPlaying: false },
    { id: 6, number: 6, playTime: 0, isPlaying: false },
  ]);

  useEffect(() => {
    const interval = setInterval(() => {
      setTables((prevTables) =>
        prevTables.map((table) =>
          table.isPlaying
            ? { ...table, playTime: table.playTime + 1 }
            : table
        )
      );
    }, 1000);

    return () => clearInterval(interval);
  }, []);

  const formatTime = (seconds: number) => {
    const hrs = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    return `${hrs.toString().padStart(2, "0")}:${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
  };

  const toggleTable = (id: number) => {
    setTables((prevTables) =>
      prevTables.map((table) =>
        table.id === id ? { ...table, isPlaying: !table.isPlaying } : table
      )
    );
  };

  const resetTable = (id: number) => {
    setTables((prevTables) =>
      prevTables.map((table) =>
        table.id === id ? { ...table, playTime: 0, isPlaying: false } : table
      )
    );
  };

  return (
    <div>
      <h2 className="mb-6">Quản lý bàn chơi</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {tables.map((table) => (
          <div
            key={table.id}
            className="bg-white rounded-2xl shadow-lg p-6 border-2 border-gray-100 hover:shadow-xl transition"
          >
            <div className="flex flex-col items-center">
              <div
                className={`w-16 h-16 rounded-full flex items-center justify-center text-white text-2xl font-bold mb-4 ${
                  table.isPlaying
                    ? "bg-gradient-to-br from-green-500 to-emerald-600"
                    : "bg-gradient-to-br from-gray-700 to-gray-900"
                }`}
              >
                {table.number}
              </div>

              <p className="text-sm text-gray-500 mb-2">Thời gian</p>
              <p className="text-3xl font-bold text-gray-900 mb-6">
                {formatTime(table.playTime)}
              </p>

              <div className="flex space-x-3 w-full">
                <button
                  onClick={() => toggleTable(table.id)}
                  className={`flex-1 flex items-center justify-center space-x-2 py-3 rounded-lg transition shadow-sm ${
                    table.isPlaying
                      ? "bg-yellow-500 hover:bg-yellow-600 text-white"
                      : "bg-gradient-to-r from-green-500 to-emerald-600 hover:from-green-600 hover:to-emerald-700 text-white"
                  }`}
                >
                  {table.isPlaying ? (
                    <>
                      <Pause size={18} />
                      <span>Dừng</span>
                    </>
                  ) : (
                    <>
                      <Play size={18} />
                      <span>Bắt đầu</span>
                    </>
                  )}
                </button>
                <button
                  onClick={() => resetTable(table.id)}
                  className="p-3 bg-gray-200 hover:bg-gray-300 rounded-lg transition"
                >
                  <RotateCcw size={18} />
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
