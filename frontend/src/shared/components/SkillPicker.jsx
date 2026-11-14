import { useState } from 'react'

/**
 * Chip list + text box with suggestions. `value` is an array of skill names.
 * Press Enter (or click Add) to add a skill; click a chip's × to remove it.
 */
export default function SkillPicker({ value, onChange, suggestions = [], placeholder = 'Add a skill' }) {
  const [text, setText] = useState('')
  const listId = 'skill-suggestions'

  const add = () => {
    const name = text.trim()
    if (!name) return
    if (!value.some((v) => v.toLowerCase() === name.toLowerCase())) {
      // use the official spelling if the skill already exists
      const known = suggestions.find((s) => s.toLowerCase() === name.toLowerCase())
      onChange([...value, known || name])
    }
    setText('')
  }

  return (
    <div>
      <div className="mb-2 flex flex-wrap gap-2">
        {value.map((s) => (
          <span key={s} className="inline-flex items-center gap-1 rounded-full bg-indigo-50 px-2.5 py-0.5 text-xs font-medium text-indigo-700">
            {s}
            <button type="button" aria-label={`Remove ${s}`} onClick={() => onChange(value.filter((v) => v !== s))} className="text-indigo-400 hover:text-indigo-700">
              ×
            </button>
          </span>
        ))}
      </div>
      <div className="flex gap-2">
        <input
          list={listId}
          value={text}
          placeholder={placeholder}
          onChange={(e) => setText(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter') {
              e.preventDefault()
              add()
            }
          }}
          className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-200"
        />
        <datalist id={listId}>
          {suggestions.map((s) => (
            <option key={s} value={s} />
          ))}
        </datalist>
        <button type="button" onClick={add} className="rounded-lg border border-slate-300 px-3 py-2 text-sm font-medium hover:bg-slate-50">
          Add
        </button>
      </div>
    </div>
  )
}
