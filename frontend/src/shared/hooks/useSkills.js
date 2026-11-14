import { useEffect, useState } from 'react'
import { api } from '../../api/client.js'

/** Loads the master skill list once. */
export default function useSkills() {
  const [skills, setSkills] = useState([])
  useEffect(() => {
    api('/skills').then(setSkills).catch(() => {})
  }, [])
  return skills
}
